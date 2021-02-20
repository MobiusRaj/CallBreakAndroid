package com.mobius.callbreakandroid.adapters;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobius.callbreakandroid.R;
import com.mobius.callbreakandroid.data_store.CashGame_TableList;
import com.mobius.callbreakandroid.data_store.PreferenceManager;
import com.mobius.callbreakandroid.interfaces.CashGameButtonListner;
import com.mobius.callbreakandroid.utility_base.C;

import org.json.JSONException;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class Adapter_CashGame_TableList extends RecyclerView.Adapter<Adapter_CashGame_TableList.ViewHolder> {

    private CashGameButtonListner listner;
    private ArrayList<CashGame_TableList> cashGame_tableLists;
    private boolean isMinEntrySet, isFromCashGame;
    private C c = C.getInstance();
    String userLevel = "L" + PreferenceManager.getUserLevelPoint();

    public Adapter_CashGame_TableList(CashGameButtonListner listner, ArrayList<CashGame_TableList> cashGame_tableLists, boolean isMinEntrySet, boolean isFromCashGame) {
        this.listner = listner;
        this.cashGame_tableLists = cashGame_tableLists;
        this.isMinEntrySet = isMinEntrySet;
        this.isFromCashGame = isFromCashGame;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(isFromCashGame ? R.layout.adapter_callbreak_table_list : R.layout.adapter_callbreak_table_list, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

//        if (isFromCashGame) {
//            holder.mainContainer.setBackgroundResource(position % 2 == 0 ? R.color.tbl_list_bg : R.color.second_tbl_list_bg);
//        } else {
            holder.mainContainer.setBackgroundResource(position % 2 == 0 ? R.color.cb_tbl_list_bg : R.color.cb_second_tbl_list_bg);
//        }
        holder.betValue.setText(String.valueOf(cashGame_tableLists.get(position).getBv()));
        holder.activPlayer.setText(String.format("(%d Players Active)", cashGame_tableLists.get(position).getActivePlayers()));
        if (isMinEntrySet) {
            holder.gameType.setText("" + cashGame_tableLists.get(position).getMinEntry());
        } else {
            if (cashGame_tableLists.get(position).getsubType().equalsIgnoreCase("101 Pool")) {
                holder.gameType.setText("101");
            } else if (cashGame_tableLists.get(position).getsubType().equalsIgnoreCase("201 Pool")) {
                holder.gameType.setText("201");
            } else {
                holder.gameType.setText(cashGame_tableLists.get(position).getsubType());
            }
        }

        if (!isFromCashGame) {
            holder.gameType.setText("Best Of 3");
        }

        holder.openTable.setText(String.format("%d/%d", cashGame_tableLists.get(position).getAp(), cashGame_tableLists.get(position).getMs()));
        try {
            if (isMinEntrySet && isFromCashGame) {
                if ((int) c.Chips >= cashGame_tableLists.get(position).getMinEntry()) {
                    holder.btnPlayNow.setVisibility(View.VISIBLE);
                    holder.btnAddCash.setVisibility(View.GONE);
                } else {
                    holder.btnAddCash.setVisibility(View.VISIBLE);
                    holder.btnPlayNow.setVisibility(View.GONE);
                }
            } else {
                if ((int) c.Chips >= Integer.parseInt(cashGame_tableLists.get(position).getBv())) {
                    holder.btnPlayNow.setVisibility(View.VISIBLE);
                    holder.btnAddCash.setVisibility(View.GONE);
                } else {
                    holder.btnAddCash.setVisibility(View.VISIBLE);
                    holder.btnPlayNow.setVisibility(View.GONE);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (!isFromCashGame && cashGame_tableLists.get(position).getBv().equalsIgnoreCase("0")) {
            if (c.practicGameFlage) {
                SetUpForPracticeGame(holder);
            } else {
                cashGame_tableLists.remove(position);
                holder.mainContainer.post(() -> new Handler().postDelayed(() -> notifyDataSetChanged(), 500));
            }
        }

        try {
            String level;
            for (int i = 0; i < cashGame_tableLists.get(position).getLoyaltyLevelArray().length(); i++) {
                level = cashGame_tableLists.get(position).getLoyaltyLevelArray().getString(i);
                if (level.equalsIgnoreCase(userLevel)) {
                    holder.icnLock.setVisibility(View.GONE);
                    holder.buttonLock.setVisibility(View.GONE);
                    break;
                } else {
                    holder.icnLock.setVisibility(holder.btnPlayNow.getVisibility() == View.VISIBLE ? View.VISIBLE : View.GONE);
                    holder.buttonLock.setVisibility(holder.btnPlayNow.getVisibility() == View.VISIBLE ? View.VISIBLE : View.GONE);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.btnPlayNow.setOnClickListener(v -> {
            if (listner != null) {
                listner.playButtonClick(cashGame_tableLists.get(position).getId(), cashGame_tableLists.get(position).getCatId());
            }
        });
        holder.btnAddCash.setOnClickListener(v -> {
            if (listner != null) {
                listner.addCashButtonClick();
            }
        });
        holder.buttonLock.setOnClickListener(v -> {
            if (listner != null) {
                listner.lockButtonClick(cashGame_tableLists.get(position).getCatId());
            }
        });
        holder.icnLock.setOnClickListener(v -> {
            if (listner != null) {
                listner.lockButtonClick(cashGame_tableLists.get(position).getCatId());
            }
        });
    }


    private void SetUpForPracticeGame(ViewHolder holder) {
        holder.mainContainer.setBackgroundResource(R.color.cb_practice_bg);
        holder.betValue.setText("0");
        holder.activPlayer.setVisibility(View.GONE);
        holder.gameType.setText("Practice");
        holder.openTable.setText("3/4");
    }

    @Override
    public int getItemCount() {
        return cashGame_tableLists.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout mainContainer;
        ImageView buttonLock, icnLock;
        TextView betValue, activPlayer, gameType,
                openTable, btnPlayNow, btnAddCash;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mainContainer = itemView.findViewById(R.id.main_container);
            betValue = itemView.findViewById(R.id.bet_value);
            activPlayer = itemView.findViewById(R.id.activ_player);
            gameType = itemView.findViewById(R.id.game_type);
            openTable = itemView.findViewById(R.id.open_table);
            btnPlayNow = itemView.findViewById(R.id.btn_play_now);
            btnAddCash = itemView.findViewById(R.id.btn_add_cash);
            buttonLock = itemView.findViewById(R.id.button_lock);
            icnLock = itemView.findViewById(R.id.icn_lock);
        }
    }
}
