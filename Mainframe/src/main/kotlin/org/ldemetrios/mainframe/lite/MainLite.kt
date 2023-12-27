package org.ldemetrios.mainframe.lite

import java.io.File
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Paths
import java.util.jar.JarFile
import kotlin.io.path.absolutePathString


val pluginRoot = "../BuiltPlugins/" // Обычно эту ссылку устанавливают во время инсталляции программы

fun main() {
    val root = File(pluginRoot)

    // Получаем список файлов внутри неё, пытаемся каждый загрузить как плагин
    val pluginList = root.listFiles()!!.map { loadPlugin(it) }.filterNotNull()

    println("Loaded ${pluginList.size} plugins: $pluginList")

    // Начиная с единицы, последовательно применяем все имеющиеся функции
    var set = setOf<Short>(1)
    var prevSize = 0
    while (set.size != prevSize) {
        prevSize = set.size

        val newSet = set.toMutableSet() // На самом деле здесь копирование
        for (x in set) for (f in pluginList) newSet.add(f(x))
        set = newSet + set // Все новые варианты + старые
    }

    if (set.size > 32) { // Если их слишком много, не будем выводить все
        println("Result: ${set.size} different numbers")
    } else {
        println("Result: $set")
    }
}

/**
 * Операции с `Method` обычно громоздкие, поэтому часто используются различного рода обёртки.
 * Здесь, тем не менее, всё не так сложно, как могло бы быть.
 *
 * NamedFunction содержит имя плагина и функцию, которую плагин предоставляет.
 */
data class NamedFunction(val name: String, val function: Method) {
    // В качестве `obj` передаём `null`, так как метод статический и не принимает неявного `this`
    operator fun invoke(x: Short) = function.invoke(null, x) as Short

    override fun toString(): String = name
}

/**
 * `loadPlugin(File)` загружает переданный файл как плагин. Он ищет статический (companion) метод, удовлетворяющий следующим критериям:
 *  * Его имя `function`,
 *  * Он принимает один Short
 *  * Он возвращает Short
 *
 *  `loadPlugin` возвращает NamedFunction, или null, если плагин не удалось загрузить.
 */
fun loadPlugin(file: File): NamedFunction? {
    val clazz = when (file.extension) {
        "class" -> loadAsClass(file) // Одиночный classfile
        "jar" -> loadAsJar(file) // Jar с манифестом
        else -> return null // Что-то непонятное
    }

    // Ищем метод с именем `function` и одним Short-ом аргументом
    val method: Method = clazz.getMethod("function", Short::class.java)

    // Благодаря файловой системе у нас не может оказаться одинаковых имён
    return NamedFunction(file.name, method)
}

/**
 * Немного магии, сейчас разберёмся.
 */
fun loadAsJar(file: File): Class<*> {
    val jarURL: URL = file.toURI().toURL()
    val classLoader = URLClassLoader(arrayOf(jarURL)) // Создаём ClassLoader для загрузки классов из jar-файла
    val jarFile: JarFile = JarFile(file.normalizedPath)
    val manifest = jarFile.manifest // Получаем доступ к MANIFEST.MF
    val functionClass = manifest.mainAttributes.getValue("Function-Class") // Ищем наш атрибут

    jarFile.close() // Нашли, jarFile нам больше не нужен. Так как это ресурс, его надо закрыть :(
    return classLoader.loadClass(functionClass) // Загружаем класс по имени, которое прочитали в манифесте
}

fun loadAsClass(file: File): Class<*> {
    return FileClassLoader.loadClassFromFile(file) // Переводим стрелки
}

/**
 * Чёртова магия, тут не спрашивайте.
 */
object FileClassLoader : ClassLoader() {
    fun loadClassFromFile(file: File): Class<*> {
        val byteArray: ByteArray = file.readBytes()
        return defineClass(null, byteArray, 0, byteArray.size)
    }
}

// Вспомогательная штука, чтобы выводить путь к файлу в нормальном виде.
val File.normalizedPath get() = Paths.get(this.path).toAbsolutePath().normalize().absolutePathString()
