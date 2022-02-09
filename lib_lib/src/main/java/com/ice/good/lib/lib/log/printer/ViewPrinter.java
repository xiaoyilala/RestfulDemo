package com.ice.good.lib.lib.log.printer;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ice.good.lib.lib.R;
import com.ice.good.lib.lib.log.base.ILogPrinter;
import com.ice.good.lib.lib.log.base.LogConfig;
import com.ice.good.lib.lib.log.base.LogType;
import com.ice.good.lib.lib.log.been.LogModel;

import java.util.ArrayList;
import java.util.List;

public class ViewPrinter implements ILogPrinter {

    private RecyclerView recyclerView;
    private LogAdapter adapter;
    private ViewPrinterProvider viewPrinterProvider;

    public ViewPrinter(Activity activity) {
        FrameLayout rootView = activity.findViewById(android.R.id.content);
        recyclerView = new RecyclerView(activity);
        adapter = new LogAdapter(LayoutInflater.from(recyclerView.getContext()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        viewPrinterProvider = new ViewPrinterProvider(rootView, recyclerView);
    }

    /**
     * ViewPrinterProvider，通过ViewPrinterProvider可以控制log视图的展示和隐藏
     *
     * @return ViewProvider
     */
    public ViewPrinterProvider getViewPrinterProvider() {
        return viewPrinterProvider;
    }

    @Override
    public void print(@NonNull LogConfig config, int level, String tag, @NonNull String printString) {
        adapter.addItem(new LogModel(System.currentTimeMillis(), level, tag, printString));
        // 滚动到对应的位置
        recyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
    }

    private static class LogAdapter extends RecyclerView.Adapter<LogViewHolder>{

        private LayoutInflater inflater;

        private List<LogModel> logs = new ArrayList<>();

        LogAdapter(LayoutInflater inflater) {
            this.inflater = inflater;
        }

        void addItem(LogModel logModel){
            logs.add(logModel);
            notifyItemInserted(logs.size()-1);
        }

        @NonNull
        @Override
        public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = inflater.inflate(R.layout.log_item, parent, false);
            return new LogViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
            LogModel logModel = logs.get(position);
            int color = getHighlightColor(logModel.level);
            holder.tagView.setTextColor(color);
            holder.messageView.setTextColor(color);

            holder.tagView.setText(logModel.getFlattened());
            holder.messageView.setText(logModel.log);
        }

        @Override
        public int getItemCount() {
            return logs.size();
        }

        /**
         * 跟进log级别获取不同的高了颜色
         *
         * @param logLevel log 级别
         * @return 高亮的颜色
         */
        private int getHighlightColor(int logLevel) {
            int highlight;
            switch (logLevel) {
                case LogType.V:
                    highlight = 0xffbbbbbb;
                    break;
                case LogType.D:
                    highlight = 0xffffffff;
                    break;
                case LogType.I:
                    highlight = 0xff6a8759;
                    break;
                case LogType.W:
                    highlight = 0xffbbb529;
                    break;
                case LogType.E:
                    highlight = 0xffff6b68;
                    break;
                default:
                    highlight = 0xffffff00;
                    break;
            }
            return highlight;
        }
    }

    private static class LogViewHolder extends RecyclerView.ViewHolder{

        TextView tagView;
        TextView messageView;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            tagView = itemView.findViewById(R.id.tag);
            messageView = itemView.findViewById(R.id.message);
        }
    }
}
