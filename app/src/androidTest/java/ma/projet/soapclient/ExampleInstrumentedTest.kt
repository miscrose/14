package ma.projet.soapclient

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import org.junit.Test
import org.junit.Assert.assertEquals
import android.content.Context

@RunWith(AndroidJUnit4::class)
class AppContextTest {

    @Test
    fun appContext_packageName_isCorrect() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context: Context = instrumentation.targetContext

        val expectedPackage = "ma.projet.soapclient"
        val actualPackage = context.packageName

        assertEquals(expectedPackage, actualPackage)
    }
}
