package ma.projet.soapclient

import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ma.projet.soapclient.adapter.CompteAdapter
import ma.projet.soapclient.beans.TypeCompte
import ma.projet.soapclient.ws.Service

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAdd: Button
    private val compteAdapter = CompteAdapter()
    private val service = Service()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViews()
        configureRecyclerView()
        attachListeners()
        fetchComptes()
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.recyclerView)
        btnAdd = findViewById(R.id.fabAdd)
    }

    private fun configureRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = compteAdapter

        compteAdapter.onDeleteClick = { compte ->
            MaterialAlertDialogBuilder(this)
                .setTitle("Supprimer le compte")
                .setMessage("Voulez-vous vraiment supprimer ce compte ?")
                .setPositiveButton("Supprimer") { _, _ ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        val deleted = service.deleteCompte(compte.id ?: 0)
                        withContext(Dispatchers.Main) {
                            if (deleted) {
                                compteAdapter.removeCompte(compte)
                                Toast.makeText(this@MainActivity, "Compte supprimé.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@MainActivity, "Erreur lors de la suppression.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                .setNegativeButton("Annuler", null)
                .show()
        }
    }

    private fun attachListeners() {
        btnAdd.setOnClickListener { showAddCompteDialog() }
    }

    private fun showAddCompteDialog() {
        val dialogView = layoutInflater.inflate(R.layout.popup, null)
        val etSolde = dialogView.findViewById<TextInputEditText>(R.id.etSolde)
        val radioCourant = dialogView.findViewById<RadioButton>(R.id.radioCourant)

        MaterialAlertDialogBuilder(this)
            .setTitle("Nouveau compte")
            .setView(dialogView)
            .setPositiveButton("Ajouter") { _, _ ->
                val solde = etSolde.text.toString().toDoubleOrNull() ?: 0.0
                val type = if (radioCourant.isChecked) TypeCompte.COURANT else TypeCompte.EPARGNE

                lifecycleScope.launch(Dispatchers.IO) {
                    val added = service.createCompte(solde, type)
                    withContext(Dispatchers.Main) {
                        if (added) {
                            Toast.makeText(this@MainActivity, "Compte ajouté.", Toast.LENGTH_SHORT).show()
                            fetchComptes()
                        } else {
                            Toast.makeText(this@MainActivity, "Erreur lors de l'ajout.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun fetchComptes() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val comptes = service.getComptes()
                withContext(Dispatchers.Main) {
                    if (comptes.isNotEmpty()) {
                        compteAdapter.updateComptes(comptes)
                    } else {
                        Toast.makeText(this@MainActivity, "Aucun compte trouvé.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Erreur: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
