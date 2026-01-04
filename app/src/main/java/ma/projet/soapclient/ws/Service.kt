package ma.projet.soapclient.ws

import ma.projet.soapclient.beans.Compte
import ma.projet.soapclient.beans.TypeCompte
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.PropertyInfo
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE
import java.text.SimpleDateFormat
import java.util.*

class Service {

    private val NAMESPACE = "http://ws.soapAcount/"
    private val URL = "http://10.0.2.2:8082/services/ws"

    private val METHOD_GET_COMPTES = "getComptes"
    private val METHOD_CREATE_COMPTE = "createCompte"
    private val METHOD_DELETE_COMPTE = "deleteCompte"

 
   
 
    fun createCompte(solde: Double, type: TypeCompte): Boolean {
        val request = SoapObject(NAMESPACE, METHOD_CREATE_COMPTE).apply {
            addProperty("solde", solde.toString())
            addProperty("type", type.name)
        }

        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11).apply {
            dotNet = false
            setOutputSoapObject(request)
        }
        val transport = HttpTransportSE(URL)

        return try {
            transport.call("", envelope)
            true
        } catch (ex: Exception) {
            ex.printStackTrace()
            false
        }
    }
 fun getComptes(): List<Compte> {
        val request = SoapObject(NAMESPACE, METHOD_GET_COMPTES)
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11).apply {
            dotNet = false
            setOutputSoapObject(request)
        }
        val transport = HttpTransportSE(URL)
        val comptesList = mutableListOf<Compte>()

        try {
            transport.call("", envelope)
            val response = envelope.bodyIn as SoapObject
            for (i in 0 until response.propertyCount) {
                val soapCompte = response.getProperty(i) as SoapObject
                val compte = Compte(
                    id = soapCompte.getPropertySafelyAsString("id")?.toLongOrNull(),
                    solde = soapCompte.getPropertySafelyAsString("solde")?.toDoubleOrNull() ?: 0.0,
                    dateCreation = SimpleDateFormat("yyyy-MM-dd").parse(
                        soapCompte.getPropertySafelyAsString("dateCreation")
                    ) ?: Date(),
                    type = TypeCompte.valueOf(
                        soapCompte.getPropertySafelyAsString("type")
                    )
                )
                comptesList.add(compte)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return comptesList
    }


    fun deleteCompte(id: Long): Boolean {
        val request = SoapObject(NAMESPACE, METHOD_DELETE_COMPTE).apply {
            val idProp = PropertyInfo().apply {
                name = "id"
                value = id
                type = PropertyInfo.LONG_CLASS
            }
            addProperty(idProp)
        }

        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11).apply {
          
            setOutputSoapObject(request)
              dotNet = false
        }

        val transport = HttpTransportSE(URL)

        return try {
            transport.call("", envelope)
            envelope.response as? Boolean ?: false
        } catch (ex: Exception) {
 
            false
        }
    }
}
