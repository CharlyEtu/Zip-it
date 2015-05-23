package com.capella.zipit.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.capella.zipit.R;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    //Définir deux variables pour spécifier le type soit un header soit un élément
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    //Tableau des titres et des icônes des éléments du burger menu
    private String mNavTitles[];
    private int mIcons[];

    /**
     * Création d'une classe ViewHolder qui hérie de RecyclerView ViewHolder
     * ViewHolder va enregistré les vues pour les recycler
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        int Holderid;

        TextView textView;
        ImageView imageView;

        /**
         * Constructeur ViewHolder qui prend une vue et le type de la vue comme paramètres
         * @param itemView
         * @param ViewType
         */
        public ViewHolder(View itemView,int ViewType) {
            super(itemView);

            //On met la vue suivant le type passé
            //Si c'est un élément
            if(ViewType == TYPE_ITEM) {

                //On récupère le textview de l'élément
                textView = (TextView) itemView.findViewById(R.id.rowText);

                //On récupère l'icône de l'élément
                imageView = (ImageView) itemView.findViewById(R.id.rowIcon);

                //On met la valeur du holder à 1 comme c'est un type élément
                Holderid = 1;
            }

            //Si c'est un header
            else{

                //On met la valeur du holder à 0 comme c'est un type header
                Holderid = 0;
            }
        }

    }

    /**
     * Constructeur de la classer MyAdapter avec comme paramètres les titres et les icônes des
     * éléments
     * @param Titles
     * @param Icons
     */
    public MyAdapter(String Titles[], int Icons[]){

        //Attribution des paramètres aux attributs
        mNavTitles = Titles;
        mIcons = Icons;
    }


    /**
     * Méthode appelée lors de la création du ViewHolder
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //Si c'est un type élément
        if (viewType == TYPE_ITEM) {

            //On récupère la vue de l'élément
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row,parent,
                    false);

            //On crée le ViewHolder propre à l'élément
            ViewHolder vhItem = new ViewHolder(v,viewType);

            //On retourne le ViewHolder de l'élément créé
            return vhItem;

        }

        //Si c'est un header
        else if (viewType == TYPE_HEADER) {

            //On récupère la vue du header
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header,parent,false);

            //On crée le ViewHolder du header
            ViewHolder vhHeader = new ViewHolder(v,viewType);

            //On retourne le ViewHolder du header
            return vhHeader; //returning the object created


        }

        //Dans tous les autres cas on retourne null
        return null;

    }

    /**
     * Méthode appelée quand on veut afficher les éléments du burger menu
     * @param holder
     * @param position
     * @Override
     */

    public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {

        //Si c'est un élément
        if(holder.Holderid ==1) {

            //On attribue le texte et l'image de l'élément
            holder.textView.setText(mNavTitles[position - 1]);
            holder.imageView.setImageResource(mIcons[position -1]);
        }
    }

    // This method returns the number of items present in the list

    /**
     * Méthode qui retourne le nombre d'éléments du burger menu
     * @return
     */
    @Override
    public int getItemCount() {
        return mNavTitles.length+1;
    }

    /**
     * Méthode qui récupère le type de l'élément du burger menu
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    /**
     * Méthode qui spécifie suivant la position si c'est un header ou pas
     * @param position
     * @return
     */
    private boolean isPositionHeader(int position) {
        return position == 0;
    }

}
