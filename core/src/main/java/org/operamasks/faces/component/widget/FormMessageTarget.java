package org.operamasks.faces.component.widget;

public enum FormMessageTarget {
    none {
        @Override
        public String toString() {
            return "none";
        }
    },
    qtip {
        @Override
        public String toString() {
            return "qtip";
        }
    },
    title {
        @Override
        public String toString() {
            return "title";
        }
    },
    under {
        @Override
        public String toString() {
            return "under";
        }
    },
    side {
        @Override
        public String toString() {
            return "side";
        }
    };
    
    public static String getSupportTargets(){
        StringBuffer sb = new StringBuffer();
        for(FormMessageTarget value : FormMessageTarget.values()){
            sb.append(value.toString());
            sb.append(",");
        }
        String allValues = sb.toString();
        return allValues.substring(0, allValues.length()-1);
    }
    
    public static Boolean isSupport(String target){
        for(FormMessageTarget value : FormMessageTarget.values()){
            if(value.toString().equals(target)){
                return true;
            }
        }
        return false;
    }
}
