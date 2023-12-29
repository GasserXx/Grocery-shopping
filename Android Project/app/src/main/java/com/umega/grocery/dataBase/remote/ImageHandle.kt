import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ImageHandle(private val context: Context) {
    suspend fun cacheImage(imageUrl: String, customFileName: String): String? =
        withContext(Dispatchers.IO) {
            try {
                val bitmap: Bitmap = Glide.with(context)
                    .asBitmap()
                    .load(imageUrl)
                    .submit()
                    .get()

                val cacheDir =
                    File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "products_cache")
                if (!cacheDir.exists()) {
                    cacheDir.mkdirs()
                }
                val customFile = File(cacheDir, customFileName)
                val outputStream = FileOutputStream(customFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.flush()
                outputStream.close()

                return@withContext customFile.absolutePath
            } catch (e:Exception) {
                e.printStackTrace()
                return@withContext null
            }
        }
    suspend fun getCachedFilePath(customFileName: String): String? =
        withContext(Dispatchers.IO) {
            val cacheDir =
                File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "products_cache")
            val customFile = File(cacheDir, customFileName)

            return@withContext if (customFile.exists()) customFile.absolutePath else null
        }
}


