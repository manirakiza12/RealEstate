package com.elite.util;

public class Events {

    public static class myPropertyUpdate {

    }

    public static class ProfileUpdate {

    }

     public static class FavProperty {

        private String propertyId;
        private String isFav;

        public String getPropertyId() {
            return propertyId;
        }

        public void setPropertyId(String propertyId) {
            this.propertyId = propertyId;
        }

        public String getIsFav() {
            return isFav;
        }

        public void setIsFav(String isFav) {
            this.isFav = isFav;
        }

    }

}
