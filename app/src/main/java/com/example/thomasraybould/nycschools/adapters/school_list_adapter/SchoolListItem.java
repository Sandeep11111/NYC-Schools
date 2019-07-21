package com.example.thomasraybould.nycschools.adapters.school_list_adapter;

import com.example.thomasraybould.nycschools.entities.Borough;

public class SchoolListItem {

    private final SchoolListItemType type;
    private final Borough borough;
    private final String titleText;
    private final Runnable onClickRunnable;

    private SchoolListItem(Builder builder) {
        type = builder.type;
        borough = builder.borough;
        titleText = builder.titleText;
        onClickRunnable = builder.runnable;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getTitleText() {
        return titleText;
    }

    public SchoolListItemType getType() {
        return type;
    }

    public Runnable getOnClickRunnable() {
        return onClickRunnable;
    }

    public Borough getBorough() {
        return borough;
    }


    public static SchoolListItem createSchoolItem(String titleText, Borough borough) {
        return new Builder()
                .type(SchoolListItemType.SCHOOL_ITEM)
                .titleText(titleText)
                .borough(borough)
                .build();
    }

    public static SchoolListItem createBoroughItem(Borough borough, Runnable onClickRunnable) {
        return SchoolListItem.newBuilder()
                .type(SchoolListItemType.BOROUGH_TITLE)
                .borough(borough)
                .titleText(borough.boroughTitle)
                .runnable(onClickRunnable)
                .build();
    }


    public static final class Builder {
        private SchoolListItemType type;
        private Borough borough;
        private String titleText;
        private Runnable runnable;

        public Builder() {
        }

        public Builder type(SchoolListItemType val) {
            type = val;
            return this;
        }

        public Builder borough(Borough val) {
            borough = val;
            return this;
        }

        public Builder titleText(String val) {
            titleText = val;
            return this;
        }

        public Builder runnable(Runnable val) {
            runnable = val;
            return this;
        }

        public SchoolListItem build() {
            return new SchoolListItem(this);
        }
    }
}