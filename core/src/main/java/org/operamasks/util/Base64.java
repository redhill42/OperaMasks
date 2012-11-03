/*
 * $Id: Base64.java,v 1.4 2007/07/02 07:37:54 jacky Exp $
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

package org.operamasks.util;

import java.io.IOException;

/**
 * This class implements a Base64 character encode/decode as specified in
 * RFC 2045.
 */
public class Base64
{
    private static final int BYTES_PER_ATOM = 3;
    private static final int CHARS_PER_ATOM = 4;

    // This array maps the characters to their 6 bit values
    private static final char pem_array[] = {
        //       0   1   2   3   4   5   6   7
                'A','B','C','D','E','F','G','H', // 0
                'I','J','K','L','M','N','O','P', // 1
                'Q','R','S','T','U','V','W','X', // 2
                'Y','Z','a','b','c','d','e','f', // 3
                'g','h','i','j','k','l','m','n', // 4
                'o','p','q','r','s','t','u','v', // 5
                'w','x','y','z','0','1','2','3', // 6
                '4','5','6','7','8','9','+','/'  // 7
        };

    private static final byte pem_convert_array[] = new byte[256];

    static {
        for (int i = 0; i < 255; i++) {
            pem_convert_array[i] = -1;
        }
        for (int i = 0; i < pem_array.length; i++) {
            pem_convert_array[pem_array[i]] = (byte)i;
        }
    }

    /**
     * Encode the input buffer into a string containing the encoded buffer.
     */
    public static String encode(byte buffer[]) {
        if (buffer == null || buffer.length == 0)
            return "";

        int inputLen = buffer.length;
        int numAtoms = inputLen / BYTES_PER_ATOM;
        if (inputLen % BYTES_PER_ATOM != 0)
            numAtoms++;
        int outputLen = numAtoms * CHARS_PER_ATOM;
        char[] output = new char[outputLen];

        int outOffset = 0, inOffset = 0;
        for (int i = 0; i < numAtoms; i++) {
            encodeAtom(output, outOffset, buffer, inOffset, (inputLen - inOffset));
            inOffset += BYTES_PER_ATOM;
            outOffset += CHARS_PER_ATOM;
        }

        return new String(output);
    }

    /**
     * EncodeAtom - Takes three bytes of input and encode it as 4
     * printable characters. Note that if the length in len is less
     * than three is encodes either one or two '=' signs to indicate
     * padding characters.
     */
    protected static void encodeAtom(char[] output, int outOffset,
                                     byte[] data, int offset, int len) {
        byte a, b, c;

        if (len == 1) {
            a = data[offset];
            b = 0;
            c = 0;
            output[outOffset]   = pem_array[(a >>> 2) & 0x3F];
            output[outOffset+1] = pem_array[((a << 4) & 0x30)];
            output[outOffset+2] = '=';
            output[outOffset+3] = '=';
        } else if (len == 2) {
            a = data[offset];
            b = data[offset+1];
            c = 0;
            output[outOffset]   = pem_array[(a >>> 2) & 0x3F];
            output[outOffset+1] = pem_array[((a << 4) & 0x30) + ((b >>> 4) & 0x0F)];
            output[outOffset+2] = pem_array[((b << 2) & 0x3C)];
            output[outOffset+3] = '=';
        } else {
            a = data[offset];
            b = data[offset+1];
            c = data[offset+2];
            output[outOffset]   = pem_array[(a >>> 2) & 0x3F];
            output[outOffset+1] = pem_array[((a << 4) & 0x30) + ((b >>> 4) & 0x0F)];
            output[outOffset+2] = pem_array[((b << 2) & 0x3C) + ((c >>> 6) & 0x03)];
            output[outOffset+3] = pem_array[c & 0x3F];
        }
    }

    /**
     * Decode the input string.
     */
    public static byte[] decode(String input) throws IOException {
        return decode(input.getBytes());
    }

    /**
     * Decode the input character array.
     */
    public static byte[] decode(byte[] input) throws IOException {
        if (input == null || input.length == 0)
            return new byte[0];

        int inputLen = input.length;
        int numAtoms = inputLen / CHARS_PER_ATOM;
        if (inputLen % CHARS_PER_ATOM != 0)
            throw new IOException("Invalid BASE64 encoded string.");
        int outputLen = numAtoms * BYTES_PER_ATOM;
        if (input[inputLen-1] == '=')
            outputLen--;
        if (input[inputLen-2] == '=')
            outputLen--;
        byte[] output = new byte[outputLen];

        int inOffset = 0, outOffset = 0;
        for (int i = 0; i < numAtoms; i++) {
            decodeAtom(output, outOffset, input, inOffset);
            inOffset += CHARS_PER_ATOM;
            outOffset += BYTES_PER_ATOM;
        }

        return output;
    }

    /**
     * Decode one Base64 atom into 1, 2, or 3 bytes of data.
     */
    protected static void decodeAtom(byte[] output, int outOffset, byte[] input, int inOffset) {
        byte a = -1, b = -1, c = -1, d = -1;

        int rem = 4;
        if (input[inOffset+3] == '=')
            rem = 3;
        if (input[inOffset+2] == '=')
            rem = 2;

        switch (rem) {
        default:
            d = pem_convert_array[input[inOffset+3] & 0xff];
            // FALLTHROUGH
        case 3:
            c = pem_convert_array[input[inOffset+2] & 0xff];
            // FALLTHROUGH
        case 2:
            b = pem_convert_array[input[inOffset+1] & 0xff];
            a = pem_convert_array[input[inOffset] & 0xff];
            break;
        }

        switch (rem) {
        case 2:
            output[outOffset] = (byte)(((a << 2) & 0xfc) | ((b >>> 4) & 3));
            break;
        case 3:
            output[outOffset]   = (byte)(((a << 2) & 0xfc) | ((b >>> 4) & 3));
            output[outOffset+1] = (byte)(((b << 4) & 0xf0) | ((c >>> 2) & 0xf));
            break;
        default:
            output[outOffset]   = (byte)(((a << 2) & 0xfc) | ((b >>> 4) & 3));
            output[outOffset+1] = (byte)(((b << 4) & 0xf0) | ((c >>> 2) & 0xf));
            output[outOffset+2] = (byte)(((c << 6) & 0xc0) | (d & 0x3f));
            break;
        }
    }
}
