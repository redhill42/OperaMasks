/*
 * $Id: HtmlResponseStateManager.java,v 1.9 2007/10/24 04:40:43 daniel Exp $
 *
 * Copyright (C) 2006 Operamasks Community.
 * Copyright (C) 2000-2006 Apusic Systems, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses.
 */

package org.operamasks.faces.render.html;

import javax.faces.render.ResponseStateManager;
import javax.faces.render.RenderKitFactory;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.context.ExternalContext;
import javax.faces.application.StateManager;
import javax.faces.FacesException;
import java.security.SecureRandom;
import java.security.MessageDigest;
import javax.crypto.SecretKey;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.InputStream;
import org.operamasks.util.Base64;
import static org.operamasks.resources.Resources.*;
import org.operamasks.faces.util.FacesUtils;

public class HtmlResponseStateManager extends ResponseStateManager
{
    private static final String STATE_SAVING_COMPRESSED_PARAM
        = "org.operamasks.faces.STATE_SAVING_COMPRESSED";
    private static final String STATE_SAVING_PASSWORD_PARAM
        = "org.operamasks.faces.STATE_SAVING_PASSWORD";

    private boolean compressed = true;
    private boolean encrypted = false;

    public HtmlResponseStateManager() {
        init();
    }

    public void writeState(FacesContext context, Object state)
        throws IOException
    {
        String viewState;
        if (state == null) {
            viewState = null;
        } else if (state instanceof String) {
            viewState = (String)state;
        } else {
            StateManager stateManager = context.getApplication().getStateManager();
            if (stateManager.isSavingStateInClient(context)) {
                viewState = encodeViewState(state);
            } else {
                viewState = context.getViewRoot().getViewId();
            }
        }

        String renderKitId = context.getApplication().getDefaultRenderKitId();
        if (renderKitId != null && renderKitId.equals(RenderKitFactory.HTML_BASIC_RENDER_KIT)) {
            renderKitId = null;
        }

        if (viewState != null || renderKitId != null) {
            writeState(context, viewState, renderKitId);
        }
    }

    protected void writeState(FacesContext context, String viewState, String renderKitId)
        throws IOException
    {
        ResponseWriter out = context.getResponseWriter();

        if (viewState != null) {
            out.startElement("input", context.getViewRoot());
            out.writeAttribute("type", "hidden", null);
            out.writeAttribute("name", VIEW_STATE_PARAM, null);
            out.writeAttribute("value", viewState, null);
            out.endElement("input");
        }

        if (renderKitId != null) {
            out.startElement("input", context.getViewRoot());
            out.writeAttribute("type", "hidden", null);
            out.writeAttribute("name", RENDER_KIT_ID_PARAM, null);
            out.writeAttribute("value", renderKitId, null);
            out.endElement("input");
        }
    }

    public Object getState(FacesContext context, String viewId) {
        Map<String,String> requestParamMap = context.getExternalContext().getRequestParameterMap();
        String viewString = requestParamMap.get(VIEW_STATE_PARAM);

        if (viewString == null || viewString.length() == 0) {
            // in case of an initial request
            return null;
        }

        StateManager stateManager = context.getApplication().getStateManager();
        if (stateManager.isSavingStateInClient(context)) {
            try {
                return decodeViewState(viewString);
            } catch (Exception ex) {
                throw new FacesException(ex);
            }
        } else {
            return viewString;
        }
    }

    public boolean isPostback(FacesContext context) {
        Map<String,String> paramMap = context.getExternalContext().getRequestParameterMap();
        return paramMap.containsKey(VIEW_STATE_PARAM) ||
               paramMap.containsKey(FacesUtils.VIEW_ID_PARAM);
    }

    private String encodeViewState(Object state)
        throws IOException
    {
        ByteArrayOutputStream bout;
        ObjectOutputStream out;

        bout = new ByteArrayOutputStream();
        if (compressed) {
            out = new ObjectOutputStream(new GZIPOutputStream(bout));
        } else {
            out = new ObjectOutputStream(bout);
        }

        out.writeObject(state);
        out.close();

        byte[] bytes = bout.toByteArray();
        if (encrypted)
            bytes = encrypt(bytes);
        return Base64.encode(bytes);
    }

    private Object decodeViewState(String viewString)
        throws IOException, ClassNotFoundException
    {
        byte[] bytes = Base64.decode(viewString);
        if (encrypted)
            bytes = decrypt(bytes);

        InputStream bin = new ByteArrayInputStream(bytes);
        if (compressed)
            bin = new GZIPInputStream(bin);

        final ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        ObjectInputStream in = new ObjectInputStream(bin) {
            @Override protected Class resolveClass(ObjectStreamClass desc)
                throws ClassNotFoundException
            {
                return Class.forName(desc.getName(), true, contextLoader);
            }};

        return in.readObject();
    }

    private static final String SECRET_KEY_ALGORITHM = "DESede";
    private static final String CIPHER_ALGORITHM = "DESede/CBC/PKCS5Padding";
    private static final String MAC_ALGORITHM = "HmacSHA1";

    private static final int KEY_LENGTH  = 24;
    private static final int MAC_LENGTH  = 20;
    private static final int IV_LENGTH   = 8;

    private static final int MAC_OFFSET  = 0;
    private static final int DATA_OFFSET = MAC_LENGTH;

    private SecureRandom random;
    private SecretKey cipherKey;
    private SecretKeySpec macKey;
    private IvParameterSpec iv;

    private byte[] encrypt(byte[] plainText) {
        try {
            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
            Mac mac = getMac();

            byte[] encrypted = cipher.doFinal(plainText);
            mac.update(encrypted);
            byte[] macBytes = mac.doFinal();
            assert macBytes.length == MAC_LENGTH;

            byte[] result = new byte[MAC_LENGTH + encrypted.length];
            System.arraycopy(macBytes, 0, result, MAC_OFFSET, MAC_LENGTH);
            System.arraycopy(encrypted, 0, result, DATA_OFFSET, encrypted.length);
            return result;
        } catch (Exception ex) {
            throw new FacesException(ex);
        }
    }

    private byte[] decrypt(byte[] secureText)
        throws IOException
    {
        try {
            Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
            Mac mac = getMac();

            mac.update(secureText, DATA_OFFSET, secureText.length - DATA_OFFSET);
            byte[] macBytes = mac.doFinal();

            assert macBytes.length == MAC_LENGTH;
            for (int i = 0; i < MAC_LENGTH; i++) {
                if (macBytes[i] != secureText[i+MAC_OFFSET]) {
                    throw new IOException(_T(JSF_VIEW_STATE_TAMPERED));
                }
            }

            return cipher.doFinal(secureText, DATA_OFFSET, secureText.length - DATA_OFFSET);
        } catch (IOException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new FacesException(ex);
        }
    }

    private void init() {
        String compression = null;
        String password = null;

        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {
            ExternalContext ectx = context.getExternalContext();
            compression = ectx.getInitParameter(STATE_SAVING_COMPRESSED_PARAM);
            password = ectx.getInitParameter(STATE_SAVING_PASSWORD_PARAM);
        }

        if (compression != null) {
            this.compressed = Boolean.valueOf(compression);
        }

        if (password != null) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA1");
                byte[] seed = md.digest(password.getBytes());

                random = SecureRandom.getInstance("SHA1PRNG");
                random.setSeed(seed);

                byte[] rawKey = new byte[KEY_LENGTH];
                byte[] rawIv = new byte[IV_LENGTH];
                byte[] rawMacKey = new byte[MAC_LENGTH];

                random.nextBytes(rawKey);
                random.nextBytes(rawIv);
                random.nextBytes(rawMacKey);

                SecretKeyFactory keygen = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM);
                DESedeKeySpec keyspec = new DESedeKeySpec(rawKey);
                this.cipherKey = keygen.generateSecret(keyspec);
                this.macKey = new SecretKeySpec(rawMacKey, MAC_ALGORITHM);
                this.iv = new IvParameterSpec(rawIv);
                this.encrypted = true;
            } catch (Exception ex) {
                throw new FacesException(ex);
            }
        }
    }

    private Cipher getCipher(int mode) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(mode, cipherKey, iv, random);
            return cipher;
        } catch (Exception ex) {
            throw new FacesException(ex);
        }
    }

    private Mac getMac() {
        try {
            Mac mac = Mac.getInstance(MAC_ALGORITHM);
            mac.init(macKey);
            return mac;
        } catch (Exception ex) {
            throw new FacesException(ex);
        }
    }

    // Deprecated APIs

    @SuppressWarnings("deprecation")
    public void writeState(FacesContext context, StateManager.SerializedView view)
        throws IOException
    {
        Object[] state = new Object[] { view.getStructure(), view.getState() };
        writeState(context, state);
    }

    @SuppressWarnings("deprecation")
    public Object getTreeStructureToRestore(FacesContext context, String viewId) {
        Object state = getState(context, viewId);
        if (state instanceof Object[]) {
            Object[] stateArray = (Object[])state;
            Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
            requestMap.put(VIEW_STATE_PARAM, stateArray[1]);
            return stateArray[0];
        } else {
            return state;
        }
    }

    @SuppressWarnings("deprecation")
    public Object getComponentStateToRestore(FacesContext context) {
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
        Object state = requestMap.get(VIEW_STATE_PARAM);
        requestMap.remove(VIEW_STATE_PARAM);
        return state;
    }
}
