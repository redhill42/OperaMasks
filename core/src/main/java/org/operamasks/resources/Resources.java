/*
 * $Id: Resources.java,v 1.36 2008/04/16 03:07:19 patrick Exp $
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

package org.operamasks.resources;

import javax.faces.context.FacesContext;
import java.util.ResourceBundle;
import java.util.Locale;
import java.text.MessageFormat;

/**
 * Utility class for i18n.
 */
public final class Resources
{
    private static final String RESOURCE_BUNDLE_NAME =
            "org.operamasks.resources.Messages";

    /**
     * Get a string from the underlying resource bundle.
     *
     * @param key the resource key
     * @return a localized and formatted string.
     */
    public static String getText(String key) {
        Locale locale;
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null && context.getViewRoot() != null) {
            locale = context.getViewRoot().getLocale();
            if (locale == null) {
                locale = Locale.getDefault();
            }
        } else {
            locale = Locale.getDefault();
        }

        ResourceBundle bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, locale);
        return bundle.getString(key);
    }

    /**
     * Get a string from the underlying resource bundle and format
     * it with the given set of arguments.
     *
     * @param key the resource key
     * @param args arguments used to format string
     * @return a localized and formatted string.
     */
    public static String getText(String key, Object... args) {
        String format = getText(key);
        return MessageFormat.format(format, args);
    }

    /**
     * Short convenient method.
     */
    public static String _T(String key) { return getText(key); }
    public static String _T(String key, Object... args) { return getText(key, args); }


    // Message keys

    public static final String JSF_LOAD_FACES_CONFIG = "JSF_LOAD_FACES_CONFIG";
    public static final String JSF_CLASS_NOT_FOUND = "JSF_CLASS_NOT_FOUND";
    public static final String JSF_UNEXPECTED_CLASS = "JSF_UNEXPECTED_CLASS";
    public static final String JSF_INSTANTIATION_ERROR = "JSF_INSTANTIATION_ERROR";
    public static final String JSF_INVALID_MANAGED_BEAN_SCOPE = "JSF_INVALID_MANAGED_BEAN_SCOPE";
    public static final String JSF_NO_SERVLET_CONTEXT = "JSF_NO_SERVLET_CONTEXT";
    public static final String JSF_ILLEGAL_SETTING_VIEWHANDLER = "JSF_ILLEGAL_SETTING_VIEWHANDLER";
    public static final String JSF_ILLEGAL_SETTING_STATEMANAGER = "JSF_ILLEGAL_SETTING_STATEMANAGER";
    public static final String JSF_ILLEGAL_ADD_ELRESOLVER = "JSF_ILLEGAL_ADD_ELRESOLVER";
    public static final String JSF_NO_SUCH_RENDER_KIT_ID = "JSF_NO_SUCH_RENDER_KIT_ID";
    public static final String JSF_NO_SUCH_COMPONENT_TYPE = "JSF_NO_SUCH_COMPONENT_TYPE";
    public static final String JSF_NO_SUCH_CONVERTER_ID = "JSF_NO_SUCH_CONVERTER_ID";
    public static final String JSF_NO_SUCH_CONVERTER_TYPE = "JSF_NO_SUCH_CONVERTER_TYPE";
    public static final String JSF_NO_SUCH_VALIDATOR_ID = "JSF_NO_SUCH_VALIDATOR_ID";
    public static final String JSF_CREATE_COMPONENT_ERROR = "JSF_CREATE_COMPONENT_ERROR";
    public static final String JSF_CREATE_MANAGED_BEAN_ERROR = "JSF_CREATE_MANAGED_BEAN_ERROR";
    public static final String JSF_CREATE_TARGET_ERROR = "JSF_CREATE_TARGET_ERROR";
    public static final String JSF_BEAN_PROPERTY_NOT_WRITEABLE = "JSF_BEAN_PROPERTY_NOT_WRITEABLE";
    public static final String JSF_BEAN_PROPERTY_NOT_ARRAY_OR_LIST = "JSF_BEAN_PROPERTY_NOT_ARRAY_OR_LIST";
    public static final String JSF_BEAN_PROPERTY_NOT_MAP = "JSF_BEAN_PROPERTY_NOT_MAP";
    public static final String JSF_DUPLICATE_COMPONENT_ID = "JSF_DUPLICATE_COMPONENT_ID";
    public static final String JSF_DUPLICATE_LIFECYCLE_ID = "JSF_DUPLICATE_LIFECYCLE_ID";
    public static final String JSF_NO_SUCH_LIFECYCLE_ID = "JSF_NO_SUCH_LIFECYCLE_ID";
    public static final String JSF_LOCALE_TYPE_EXPECTED = "JSF_LOCALE_TYPE_EXPECTED";
    public static final String JSF_TIMEZONE_TYPE_EXPECTED = "JSF_TIMEZONE_TYPE_EXPECTED";
    public static final String JSF_VIEW_STATE_TAMPERED = "JSF_VIEW_STATE_TAMPERED";
    public static final String JSF_NOT_NESTED_IN_FACES_TAG = "JSF_NOT_NESTED_IN_FACES_TAG";
    public static final String JSF_NO_COMPONENT_FOR_FACES_TAG = "JSF_NO_COMPONENT_FOR_FACES_TAG";
    public static final String JSF_NOT_NESTED_IN_ACTION_TAG = "JSF_NOT_NESTED_IN_ACTION_TAG";
    public static final String JSF_NOT_NESTED_IN_INPUT_TAG = "JSF_NOT_NESTED_IN_INPUT_TAG";
    public static final String JSF_NESTED_FORM = "JSF_NESTED_FORM";
    public static final String JSF_EXPECT_BASENAME_AND_BAR_ATTRIBUTES = "JSF_EXPECT_BASENAME_AND_BAR_ATTRIBUTES";
    public static final String JSF_RESOURCE_BUNDLE_NOT_FOUND = "JSF_RESOURCE_BUNDLE_NOT_FOUND";
    public static final String JSF_REQUIRED_CONVERTER_ATTRIBUTES = "JSF_REQUIRED_CONVERTER_ATTRIBUTES";
    public static final String JSF_REQUIRED_VALIDATOR_ATTRIBUTES = "JSF_REQUIRED_VALIDATOR_ATTRIBUTES";
    public static final String JSF_REQUIRED_ACTION_LISTENER_ATTRIBUTES = "JSF_REQUIRED_ACTION_LISTENER_ATTRIBUTES";
    public static final String JSF_REQUIRED_VALUE_CHANGE_LISTENER_ATTRIBUTES = "JSF_REQUIRED_VALUE_CHANGE_LISTENER_ATTRIBUTES";
    public static final String JSF_REQUIRED_PHASE_LISTENER_ATTRIBUTES = "JSF_REQUIRED_PHASE_LISTENER_ATTRIBUTES";
    public static final String JSF_BIGDECIMAL_CONVERTER = "JSF_BIGDECIMAL_CONVERTER";
    public static final String JSF_BIGINTEGER_CONVERTER = "JSF_BIGINTEGER_CONVERTER";
    public static final String JSF_BYTE_CONVERTER = "JSF_BYTE_CONVERTER";
    public static final String JSF_DATETIME_CONVERTER = "JSF_DATETIME_CONVERTER";
    public static final String JSF_DATETIME_CONVERTER_GENERAL_EXCEPTION = "JSF_DATETIME_CONVERTER_GENERAL_EXCEPTION";
    public static final String JSF_DOUBLE_CONVERTER = "JSF_DOUBLE_CONVERTER";
    public static final String JSF_FLOAT_CONVERTER = "JSF_FLOAT_CONVERTER";
    public static final String JSF_INTEGER_CONVERTER = "JSF_INTEGER_CONVERTER";
    public static final String JSF_LONG_CONVERTER = "JSF_LONG_CONVERTER";
    public static final String JSF_SHORT_CONVERTER = "JSF_SHORT_CONVERTER";
    public static final String JSF_VALIDATE_REQUIRED = "JSF_VALIDATE_REQUIRED";
    public static final String JSF_VALIDATE_LENGTH_MINIMUM = "JSF_VALIDATE_LENGTH_MINIMUM";
    public static final String JSF_VALIDATE_LENGTH_MAXIMUM = "JSF_VALIDATE_LENGTH_MAXIMUM";
    public static final String JSF_VALIDATE_LENGTH_NOT_IN_RANGE = "JSF_VALIDATE_LENGTH_NOT_IN_RANGE";
    public static final String JSF_VALIDATE_LONG_RANGE_MINIMUM = "JSF_VALIDATE_LONG_RANGE_MINIMUM";
    public static final String JSF_VALIDATE_LONG_RANGE_MAXIMUM = "JSF_VALIDATE_LONG_RANGE_MAXIMUM";
    public static final String JSF_VALIDATE_LONG_RANGE_NOT_IN_RANGE = "JSF_VALIDATE_LONG_RANGE_NOT_IN_RANGE";
    public static final String JSF_VALIDATE_DOUBLE_RANGE_MINIMUM = "JSF_VALIDATE_DOUBLE_RANGE_MINIMUM";
    public static final String JSF_VALIDATE_DOUBLE_RANGE_MAXIMUM = "JSF_VALIDATE_DOUBLE_RANGE_MAXIMUM";
    public static final String JSF_VALIDATE_DOUBLE_RANGE_NOT_IN_RANGE = "JSF_VALIDATE_DOUBLE_RANGE_NOT_IN_RANGE";
    public static final String JSF_VALIDATE_REGEXP_NOT_MATCH = "JSF_VALIDATE_REGEXP_NOT_MATCH";
    public static final String JSF_VALIDATE_ILLEGAL_DATE_FORMAT = "JSF_VALIDATE_ILLEGAL_DATE_FORMAT";
    public static final String JSF_CLIENT_VALIDATE_ERROR = "JSF_CLIENT_VALIDATE_ERROR";
    public static final String JSF_CYCLIC_MANAGEDBEAN_REFERENCE="JSF_CYCLIC_MANAGEDBEAN_REFERENCE";
    public static final String JSF_UPLOADING_EXCEEDS_MAX_SIZE = "JSF_UPLOADING_EXCEEDS_MAX_SIZE";
    public static final String MVB_BIND_FIELD_AND_METHOD = "MVB_BIND_FIELD_AND_METHOD";
    public static final String MVB_BIND_READ_AND_WRITE = "MVB_BIND_READ_AND_WRITE";
    public static final String MVB_INVALID_ACCESSOR_METHOD = "MVB_INVALID_ACCESSOR_METHOD";
    public static final String MVB_INVALID_READ_METHOD = "MVB_INVALID_READ_METHOD";
    public static final String MVB_INVALID_SELECT_ITEMS_TYPE = "MVB_INVALID_SELECT_ITEMS_TYPE";
    public static final String MVB_LOCAL_STRING_KEY_NOT_FOUND = "MVB_LOCAL_STRING_KEY_NOT_FOUND";
    public static final String MVB_INVALID_ACTION_METHOD = "MVB_INVALID_ACTION_METHOD";
    public static final String MVB_INVALID_ACTION_LISTENER_METHOD = "MVB_INVALID_ACTION_LISTENER_METHOD";
    public static final String MVB_INVALID_TREE_EVENT_LISTENER_METHOD = "MVB_INVALID_TREE_EVENT_LISTENER_METHOD";
    public static final String MVB_ASYNC_TREE_NULL_TREE_ID = "MVB_ASYNC_TREE_NULL_TREE_ID";
    public static final String MVB_TREE_EVENT_LISTENER_NULL_TREE_ID = "MVB_TREE_EVENT_LISTENER_NULL_TREE_ID";
    public static final String MVB_INVALID_ASYNC_TREE_METHOD = "MVB_INVALID_ASYNC_TREE_METHOD";
    public static final String MVB_INVALID_CONVERT_METHOD = "MVB_INVALID_CONVERT_METHOD";
    public static final String MVB_INVALID_FORMAT_METHOD = "MVB_INVALID_FORMAT_METHOD";
    public static final String MVB_CONVERT_OR_FORMAT_METHOD_NOT_FOUND = "MVB_CONVERT_OR_FORMAT_METHOD_NOT_FOUND";
    public static final String MVB_INVALID_VALIDATE_METHOD = "MVB_INVALID_VALIDATE_METHOD";
    public static final String MVB_VALIDATE_METHOD_NOT_FOUND = "MVB_VALIDATE_METHOD_NOT_FOUND";
    public static final String MVB_INVALID_PHASE_LISTENER_METHOD = "MVB_INVALID_PHASE_LISTENER_METHOD";
    public static final String MVB_INVALID_BEFORE_RENDER_METHOD = "MVB_INVALID_BEFORE_RENDER_METHOD";
    public static final String MVB_INVALID_AFTER_RENDER_METHOD = "MVB_INVALID_AFTER_RENDER_METHOD";
    public static final String MVB_CONVERTER_PROPERTY_NOT_FOUND = "MVB_CONVERTER_PROPERTY_NOT_FOUND";
    public static final String MVB_CONVERTER_PROPERTY_READONLY = "MVB_CONVERTER_PROPERTY_READONLY";
    public static final String MVB_SET_CONVERTER_PROPERTY_FAILED = "MVB_SET_CONVERTER_PROPERTY_FAILED";
    public static final String MVB_VALIDATOR_PROPERTY_NOT_FOUND = "MVB_VALIDATOR_PROPERTY_NOT_FOUND";
    public static final String MVB_VALIDATOR_PROPERTY_READONLY = "MVB_VALIDATOR_PROPERTY_READONLY";
    public static final String MVB_SET_VALIDATOR_PROPERTY_FAILED = "MVB_SET_VALIDATOR_PROPERTY_FAILED";
    public static final String MVB_PROPERTY_INJECTION_FAILED = "MVB_PROPERTY_INJECTION_FAILED";
    public static final String MVB_MISSING_COMPONENT_ATTRIBUTE = "MVB_MISSING_COMPONENT_ATTRIBUTE";
    public static final String UI_PAGING_LINK_FIRST_PAGE = "UI_PAGING_LINK_FIRST_PAGE";
    public static final String UI_PAGING_LINK_LAST_PAGE = "UI_PAGING_LINK_LAST_PAGE";
    public static final String UI_PAGING_LINK_PREV_PAGE = "UI_PAGING_LINK_PREV_PAGE";
    public static final String UI_PAGING_LINK_NEXT_PAGE = "UI_PAGING_LINK_NEXT_PAGE";
    public static final String UI_DATEFIELD_BINDINGTYPE_MISMATCH = "UI_DATEFIELD_BINDINGTYPE_MISMATCH";
    public static final String UI_CHART_NO_DATA_SERIES_ASSOCIATED = "UI_CHART_NO_DATA_SERIES_ASSOCIATED";
    public static final String UI_CHART_INCOMPATIBLE_DATA_SERIES = "UI_CHART_INCOMPATIBLE_DATA_SERIES";
    public static final String UI_CHART_INCOMPATIBLE_COMMON_DATA_SERIES = "UI_CHART_INCOMPATIBLE_COMMON_DATA_SERIES";
    public static final String UI_FILE_UPLOAD_BROWSE = "UI_FILE_UPLOAD_BROWSE";
    public static final String UI_FILE_UPLOAD_START_MESSAGE = "UI_FILE_UPLOAD_START_MESSAGE";
    public static final String UI_FILE_UPLOAD_UPLOADING_MESSAGE = "UI_FILE_UPLOAD_UPLOADING_MESSAGE";
    public static final String UI_FILE_UPLOAD_COMPLETE_MESSAGE = "UI_FILE_UPLOAD_COMPLETE_MESSAGE";
    public static final String UI_FILE_UPLOAD_ERROR_MESSAGE = "UI_FILE_UPLOAD_ERROR_MESSAGE";
    public static final String UI_FORM_SELECTITEM_TYPEERROR_MESSAGE = "UI_FORM_SELECTITEM_TYPEERROR_MESSAGE";
    public static final String UI_FORM_SELECTITEMS_TYPEERROR_MESSAGE = "UI_FORM_SELECTITEMS_TYPEERROR_MESSAGE";
    public static final String UI_FORM_SELECTITEMS_VALUE_TYPEERROR_MESSAGE = "UI_FORM_SELECTITEMS_VALUE_TYPEERROR_MESSAGE";
    public static final String UI_FORM_SELECTITEMS_INCHECKBOXGROUP_TYPEERROR_MESSAGE = "UI_FORM_SELECTITEMS_INCHECKBOXGROUP_TYPEERROR_MESSAGE";
    public static final String UI_UIINPUT_EL_UNKNOWN = "UI_UIINPUT_UPDATE_UNKNOWN";
    public static final String UI_UIINPUT_UPDATEMODEL_ERROR = "UI_UIINPUT_UPDATEMODEL_ERROR";
    public static final String UI_LAYOUT_CHILD_INCORRECT_TYPE="UI_LAYOUT_CHILD_INCORRECT_TYPE";
    public static final String UI_BORDERLAYOUT_DUPLICATE_REGION="UI_BORDERLAYOUT_DUPLICATE_REGION";
    public static final String UI_BORDERLAYOUT_UNDEFINED_REGION="UI_BORDERLAYOUT_UNDEFINED_REGION";
    public static final String UI_BORDERLAYOUT_MISSING_CENTER="UI_BORDERLAYOUT_MISSING_CENTER";
    public static final String UI_MISSING_PARENT_FORM_WARNING="UI_MISSING_PARENT_FORM_WARNING";
    public static final String UI_MISSING_PAGE_PARENT="UI_MISSING_PAGE_PARENT";
    public static final String UI_UNEXPECTED_ATTRIBUTE_VALUE="UI_UNEXPECTED_ATTRIBUTE_VALUE";
    public static final String UI_IGNORING_NESTED_PAGE_TAG="UI_IGNORING_NESTED_PAGE_TAG";
    public static final String IOVC_INIT_CALLBACK_EMPTYNAME = "IOVC_INIT_CALLBACK_EMPTYNAME";
    public static final String IOVC_MISSING_GETTER="IOVC_MISSING_GETTER";

//    TODO: fix me: the prefix 'INJ' is appropriate?  by zhangyong
    public static final String INJ_BEAN_REF_SCOPE_INVALID = "INJ_BEAN_REF_SCOPE_INVALID";

    public static final String JSF_DUPPLICATE_MANAGED_BEAN = "JSF_DUPPLICATE_MANAGED_BEAN";

    private Resources() {}
}
