package com.example.thomasraybould.nycschools.adapters.school_list_adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thomasraybould.nycschools.R;
import com.example.thomasraybould.nycschools.databinding.BoroughListItemBinding;
import com.example.thomasraybould.nycschools.databinding.SatScoreListItemBinding;
import com.example.thomasraybould.nycschools.databinding.SchoolListItemBinding;
import com.example.thomasraybould.nycschools.entities.Borough;
import com.example.thomasraybould.nycschools.entities.SatScoreData;
import com.example.thomasraybould.nycschools.entities.School;
import com.example.thomasraybould.nycschools.util.StringUtil;

import java.util.List;

import static com.example.thomasraybould.nycschools.adapters.school_list_adapter.SchoolListItemType.BOROUGH_TITLE;
import static com.example.thomasraybould.nycschools.adapters.school_list_adapter.SchoolListItemType.SAT_SCORE_ITEM;
import static com.example.thomasraybould.nycschools.adapters.school_list_adapter.SchoolListItemType.SCHOOL_ITEM;

public class SchoolListAdapter extends RecyclerView.Adapter<SchoolListAdapter.ViewHolder> {

    private final OnSchoolListItemSelectedListener listener;
    private final List<SchoolListItemUiModel> schoolListItemUiModels;
    private final Context context;

    private enum LoadingPayLoad {
        LOADING_PAY_LOAD
    }

    private SchoolListAdapter(OnSchoolListItemSelectedListener listener, Context context, List<SchoolListItemUiModel> schoolListItemUiModels) {
        this.listener = listener;
        this.schoolListItemUiModels = schoolListItemUiModels;
        this.context = context;
    }

    public static SchoolListAdapter createSchoolListAdapter(OnSchoolListItemSelectedListener listener, Context context, List<SchoolListItemUiModel> schoolListItemUiModels) {
        return new SchoolListAdapter(listener, context, schoolListItemUiModels);
    }

    public int addSchoolItemsForBorough(List<SchoolListItemUiModel> newItems, Borough borough) {

        //find the title for the borough and add new items underneath
        int insertTarget = -1;
        for (int i = 0; i < schoolListItemUiModels.size(); i++) {
            SchoolListItemUiModel schoolListItemUiModel = schoolListItemUiModels.get(i);
            if (schoolListItemUiModel.getType() == BOROUGH_TITLE && schoolListItemUiModel.getBorough() == borough) {
                schoolListItemUiModel.setSelected(true);
                insertTarget = i + 1;
            }
        }

        if (insertTarget > -1) {
            schoolListItemUiModels.addAll(insertTarget, newItems);
            notifyItemRangeInserted(insertTarget, newItems.size());
        }

        return insertTarget;
    }


    /**
     * Searching for the target school that was selected and then
     * adding a score item underneath and then returning the position
     * of the school.
     *
     * @param scoreItem
     * @return
     */
    public int addScoreItemForSchool(SchoolListItemUiModel scoreItem) {
        String targetDbn = scoreItem.getSchool().getDbn();

        int insertTarget = -1;
        for (int i = 0; i < schoolListItemUiModels.size(); i++) {
            SchoolListItemUiModel schoolListItemUiModel = schoolListItemUiModels.get(i);
            if (schoolListItemUiModel.getType() != SCHOOL_ITEM) {
                continue;
            }
            if (targetDbn.equals(schoolListItemUiModel.getSchool().getDbn())) {
                insertTarget = i + 1;
            }
        }

        if (insertTarget > -1) {
            schoolListItemUiModels.add(insertTarget, scoreItem);
            notifyItemInserted(insertTarget);
        }

        return insertTarget;
    }

    public void removeScoreItem(String targetDbn) {

        for (int i = 0; i < schoolListItemUiModels.size(); i++) {
            SchoolListItemUiModel schoolListItemUiModel = schoolListItemUiModels.get(i);
            School school = schoolListItemUiModel.getSchool();

            String schoolItemDbn;

            schoolItemDbn = school != null ? school.getDbn() : null;

            if (targetDbn.equals(schoolItemDbn)) {
                if (schoolListItemUiModel.getType() == SAT_SCORE_ITEM) {
                    schoolListItemUiModels.remove(i);
                    notifyItemRemoved(i);
                    break;
                }

            }
        }

    }

    public void changeLoadingStatusOfBorough(Borough borough, boolean isLoading) {
        for (int i = 0; i < schoolListItemUiModels.size(); i++) {
            SchoolListItemUiModel schoolListItemUiModel = schoolListItemUiModels.get(i);
            if (schoolListItemUiModel.getType() == BOROUGH_TITLE && schoolListItemUiModel.getBorough() == borough) {
                schoolListItemUiModel.setLoading(isLoading);
                notifyItemChanged(i, LoadingPayLoad.LOADING_PAY_LOAD);
                break;
            }
        }
    }

    public void removeItemsForBorough(Borough borough) {
        //go through list in reverse to remove items
        for (int i = schoolListItemUiModels.size() - 1; i >= 0; i--) {
            SchoolListItemUiModel schoolListItemUiModel = schoolListItemUiModels.get(i);

            if (schoolListItemUiModel.getType() != BOROUGH_TITLE && schoolListItemUiModel.getBorough() == borough) {
                schoolListItemUiModels.remove(i);
                notifyItemRemoved(i);
            }

        }
    }

    public List<SchoolListItemUiModel> getCurrentList() {
        return schoolListItemUiModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        if (viewType == BOROUGH_TITLE.ordinal()) {
            BoroughListItemBinding itemBinding = BoroughListItemBinding.inflate(layoutInflater, parent, false);
            return new ViewHolder(itemBinding);
        } else if (viewType == SAT_SCORE_ITEM.ordinal()) {
            SatScoreListItemBinding itemBinding = SatScoreListItemBinding.inflate(layoutInflater, parent, false);
            return new ViewHolder(itemBinding);
        } else {
            SchoolListItemBinding itemBinding = SchoolListItemBinding.inflate(layoutInflater, parent, false);
            return new ViewHolder(itemBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty() && payloads.get(0) == LoadingPayLoad.LOADING_PAY_LOAD) {
            SchoolListItemUiModel schoolListItemUiModel = schoolListItemUiModels.get(position);
            if (schoolListItemUiModel.getType() == BOROUGH_TITLE) {
                holder.boroughListItemBinding.setUiModel(schoolListItemUiModel);
            }
            return;
        }
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SchoolListItemUiModel schoolListItemUiModel = schoolListItemUiModels.get(position);
        if (schoolListItemUiModel.getType() == BOROUGH_TITLE) {
            holder.bindBorough(schoolListItemUiModel, context, listener);
        } else if (schoolListItemUiModel.getType() == SAT_SCORE_ITEM) {
            holder.satScoreListItemBinding.setUiModel(schoolListItemUiModel);
        } else {
            holder.bindSchool(schoolListItemUiModel, listener);
        }
    }

    @Override
    public int getItemCount() {
        return schoolListItemUiModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        return schoolListItemUiModels.get(position).getType().ordinal();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        BoroughListItemBinding boroughListItemBinding;
        SchoolListItemBinding schoolListItemBinding;
        SatScoreListItemBinding satScoreListItemBinding;

        ViewHolder(BoroughListItemBinding boroughListItemBinding) {
            super(boroughListItemBinding.getRoot());
            this.boroughListItemBinding = boroughListItemBinding;
        }

        ViewHolder(SchoolListItemBinding schoolListItemBinding) {
            super(schoolListItemBinding.getRoot());
            this.schoolListItemBinding = schoolListItemBinding;
        }

        ViewHolder(SatScoreListItemBinding satScoreListItemBinding) {
            super(satScoreListItemBinding.getRoot());
            this.satScoreListItemBinding = satScoreListItemBinding;
        }

        void bindSchool(SchoolListItemUiModel schoolListItemUiModel, OnSchoolListItemSelectedListener listener) {
            schoolListItemBinding.setUiModel(schoolListItemUiModel);
            itemView.setOnClickListener((v) -> {
                listener.onSchoolListItemSelected(schoolListItemUiModel);
                schoolListItemUiModel.setSelected(!schoolListItemUiModel.isSelected());
            });
        }

        void bindBorough(SchoolListItemUiModel schoolListItemUiModel, Context context, OnSchoolListItemSelectedListener listener) {
            boroughListItemBinding.setUiModel(schoolListItemUiModel);

            boroughListItemBinding.getRoot().setOnClickListener((v) -> {
                listener.onSchoolListItemSelected(schoolListItemUiModel);
                schoolListItemUiModel.setSelected(!schoolListItemUiModel.isSelected());
            });

            boroughListItemBinding.progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context, R.color.progress_bar_color), PorterDuff.Mode.SRC_IN);
        }

    }

}
