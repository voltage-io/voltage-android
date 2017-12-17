package io.voltage.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.pivotal.arca.provider.ColumnName;
import io.voltage.app.application.VoltageContentProvider.ImageSearchView;

public class ImageResponse {

    protected interface Fields {
        String DATA = "data";
    }

    @SerializedName(Fields.DATA)
    private List<Result> mData;

    public List<Result> getResults() {
        return mData;
    }

    public static class Result {
        protected interface Fields {
            String ID = "id";
            String TYPE = "type";
            String RATING = "rating";
            String IMAGES = "images";
        }

        @SerializedName(Fields.ID)
        private String mId;

        @SerializedName(Fields.TYPE)
        private String mType;

        @SerializedName(Fields.RATING)
        private String mRating;

        @SerializedName(Fields.IMAGES)
        private Images mImages;

        @ColumnName(ImageSearchView.Columns.IMAGE_URL)
        public String getImageUrl() {
            return mImages != null ? mImages.getImageUrl() : null;
        }

        public static class Images {
            protected interface Fields {
                String FIXED_HEIGHT = "fixed_height";
                String FIXED_WIDTH = "fixed_width";
                String ORIGINAL = "original";
            }

            @SerializedName(Fields.FIXED_HEIGHT)
            private Size mFixedHeight;

            @SerializedName(Fields.FIXED_WIDTH)
            private Size mFixedWidth;

            @SerializedName(Fields.ORIGINAL)
            private Size mOriginal;

            public String getImageUrl() {
                return mFixedWidth != null ? mFixedWidth.getUrl() : null;
            }

            public static class Size {
                protected interface Fields {
                    String URL = "url";
                    String WIDTH = "width";
                    String HEIGHT = "height";
                }

                @SerializedName(Fields.URL)
                private String mUrl;

                @SerializedName(Fields.WIDTH)
                private String mWidth;

                @SerializedName(Fields.HEIGHT)
                private String mHeight;

                public String getUrl() {
                    return mUrl;
                }
            }
        }
    }
}
