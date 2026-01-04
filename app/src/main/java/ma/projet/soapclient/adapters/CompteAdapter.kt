package ma.projet.soapclient.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.TextView
import com.google.android.material.chip.Chip
import com.google.android.material.button.MaterialButton
import ma.projet.soapclient.R
import ma.projet.soapclient.beans.Compte
import java.text.SimpleDateFormat
import java.util.*

class CompteAdapter : RecyclerView.Adapter<CompteAdapter.ViewHolder>() {

    private val comptesList = mutableListOf<Compte>()

    var onEditClick: ((Compte) -> Unit)? = null
    var onDeleteClick: ((Compte) -> Unit)? = null

    fun updateComptes(newList: List<Compte>) {
        comptesList.clear()
        comptesList.addAll(newList)
        notifyDataSetChanged()
    }

    fun removeCompte(compte: Compte) {
        val index = comptesList.indexOf(compte)
        if (index >= 0) {
            comptesList.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val compte = comptesList[position]
        holder.bind(compte)
    }

    override fun getItemCount(): Int = comptesList.size

    inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val textId: TextView = item.findViewById(R.id.textId)
        private val textSolde: TextView = item.findViewById(R.id.textSolde)
        private val textType: Chip = item.findViewById(R.id.textType)
        private val textDate: TextView = item.findViewById(R.id.textDate)
        private val btnEdit: MaterialButton = item.findViewById(R.id.btnEdit)
        private val btnDelete: MaterialButton = item.findViewById(R.id.btnDelete)

        fun bind(compte: Compte) {
            textId.text = "Compte Numéro ${compte.id}"
            textSolde.text = "${compte.solde} DH"
            textType.text = compte.type.name
            textDate.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(compte.dateCreation)

            btnEdit.setOnClickListener { onEditClick?.invoke(compte) }
            btnDelete.setOnClickListener { onDeleteClick?.invoke(compte) }
        }
    }
}
