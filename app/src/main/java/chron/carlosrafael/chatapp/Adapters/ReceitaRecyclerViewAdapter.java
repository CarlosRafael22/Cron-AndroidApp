package chron.carlosrafael.chatapp.Adapters;

/**
 * Created by CarlosRafael on 20/02/2017.
 */

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import chron.carlosrafael.chatapp.Fragmentos.ReceitaFragment;
import chron.carlosrafael.chatapp.Fragmentos.dummy.DummyContent;
import chron.carlosrafael.chatapp.Models.Receita;
import chron.carlosrafael.chatapp.R;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyContent.DummyItem} and makes a call to the
 * specified {@link ReceitaFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ReceitaRecyclerViewAdapter extends RecyclerView.Adapter<ReceitaRecyclerViewAdapter.ViewHolder> {

    private List<Receita> mReceitas;
    private final ReceitaFragment.OnListFragmentInteractionListener mListener;

    public ReceitaRecyclerViewAdapter(List<Receita> mReceitas, ReceitaFragment.OnListFragmentInteractionListener listener) {
        this.mReceitas = mReceitas;
        this.mListener = listener;
    }

    @Override
    public ReceitaRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_receita_item, parent, false);
        return new ReceitaRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.receita = mReceitas.get(position);
        holder.nome_da_receitaView.setText(mReceitas.get(position).getNome_receita());
        holder.categoriaView.setText(mReceitas.get(position).getCategoria());
        holder.receita_idView.setText(String.valueOf(mReceitas.get(position).getId()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != mListener){
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.receita);
                }
            }
        });

    }

    public void refresh(List<Receita> receitas)
    {
        mReceitas = receitas;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mReceitas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView receita_idView;
        public final TextView nome_da_receitaView;
        public final TextView categoriaView;
        public Receita receita;
        //public DummyContent.DummyItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            receita_idView = (TextView) view.findViewById(R.id.receita_id);
            nome_da_receitaView = (TextView) view.findViewById(R.id.nome_da_receita);
            categoriaView = (TextView) view.findViewById(R.id.categoria);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + nome_da_receitaView.getText() + "'";
        }
    }
}

