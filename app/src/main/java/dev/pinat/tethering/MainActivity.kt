package dev.pinat.tethering

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.lsposed.hiddenapibypass.HiddenApiBypass
import java.lang.reflect.Proxy
import java.util.concurrent.Executor


class MainActivity : AppCompatActivity() {
    // https://android.googlesource.com/platform/packages/modules/Connectivity/+/refs/heads/master/Tethering/common/TetheringLib/src/android/net/TetheringManager.java
    private lateinit var tethering: Any

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tethering = getSystemService("tethering")
        val methods = HiddenApiBypass.getDeclaredMethods(tethering.javaClass)
        methods.forEach { any -> println(any) }
    }

    fun getTetheredInterfaces(view: View) {
        val method = HiddenApiBypass.getDeclaredMethod(tethering.javaClass, "getTetheredIfaces")
        val interfaces = method.invoke(tethering) as Array<*>
        interfaces.forEach(::println)
        findViewById<TextView>(R.id.interfaces).text = interfaces.contentToString()
    }

    @SuppressLint("WrongConstant", "PrivateApi")
    fun startTetheringButton(view: View) {
        findViewById<TextView>(R.id.tetheringResult).text = ""
        val executor = Executor(Runnable::run)
        val callback = Proxy.newProxyInstance(
            Class.forName("android.net.TetheringManager\$StartTetheringCallback").classLoader,
            arrayOf(
                Class.forName("android.net.TetheringManager\$StartTetheringCallback")
            )
        ) { _, method, args ->
            val methodName = method.name
            val classes = method.parameterTypes

            if (methodName == "onTetheringStarted") {
                findViewById<TextView>(R.id.tetheringResult).text = getString(R.string.tethering_started)
            } else if (methodName == "onTetheringFailed") {
                var result = getString(R.string.tethering_failed)
                if (classes[0] == Int::class.javaPrimitiveType) {
                    result = result.plus(": ").plus(args[0])
                    println(args[0])
                }
                findViewById<TextView>(R.id.tetheringResult).text = result
            }
        }
        HiddenApiBypass.getDeclaredMethod(
            tethering.javaClass,
            "startTethering",
            Int::class.javaPrimitiveType,
            Executor::class.java,
            Class.forName("android.net.TetheringManager\$StartTetheringCallback")
        ).invoke(tethering, 5, executor, callback)
    }
}
