package org.ldemetrios.mainframe

import java.io.File
import java.lang.reflect.InvocationTargetException
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
    // Если это не папка, мы проиграли
    if (!root.isDirectory) {
        println("Invalid plugin root directory: ${root.normalizedPath} is not directory")
        return
    }
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
    operator fun invoke(x: Short) = try {
        function.invoke(null, x) as Short // В качестве `obj` передаём `null`, так как метод статический и не принимает неявного `this`
    } catch (e: InvocationTargetException) {
        // Внутри вызова метода случилась какая-то ошибка
        throw e.targetException // Именно её здесь достаём и бросаем
    }

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
    try {
        val clazz = when (file.extension) {
            "class" -> loadAsClass(file) // Одиночный classfile
            "jar" -> loadAsJar(file) // Jar с манифестом
            else -> return null // Что-то непонятное
        }
        if (clazz == null) { // Не удалось загрузить класс
            println("Failed to load class")
            return null
        }

        val method: Method = try {
            // Пытаемся найти метод с именем `function` и одним Short-ом аргументом
            clazz.getMethod("function", Short::class.java)
        } catch (e: NoSuchMethodException) {
            // Не нашли
            println("There is no such method in the class, specified by the manifest")
            return null
        }

        // Благодаря файловой системе у нас не может оказаться одинаковых имён
        return checkAndConstruct(file.name, method) // Проверяем корректность Method-а и строим NamedFunction
    } catch (e: Exception) {
        // Случилось что-то непредвиденное
        println("Error loading plugin from ${file.normalizedPath}:")
        e.printStackTrace(System.out) // Выводим ошибку в консоль
        return null
    }
}

/**
 * Немного магии, сейчас разберёмся.
 */
fun loadAsJar(file: File): Class<*>? {
    try {
        val jarURL: URL = file.toURI().toURL()
        val classLoader = URLClassLoader(arrayOf(jarURL)) // Создаём ClassLoader для загрузки классов из jar-файла

        val jarFile: JarFile = JarFile(file.normalizedPath)
        val manifest = jarFile.manifest // Получаем доступ к MANIFEST.MF

        val functionClass = if (manifest != null) {
            manifest.mainAttributes.getValue("Function-Class") // Ищем наш атрибут
        } else {
            println("Manifest file not found in ${file.normalizedPath}")
            return null
        }
        jarFile.close() // Нашли, jarFile нам больше не нужен. Так как это ресурс, его надо закрыть :(

        return classLoader.loadClass(functionClass) // Загружаем класс по имени, которое прочитали в манифесте
    } catch (e: ClassNotFoundException) {
        // Не нашлось. Манифест неправильный.
        println("Class not found")
        return null
    }
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

/**
 * Проверяем, что это то, что нам нужно
 */
fun checkAndConstruct(name: String, method: Method): NamedFunction? {
    // Проверяем, что метод возвращает Short
    if (method.returnType != Short::class.java) {
        println("Plugin function must return Short")
        return null
    }

    // Проверяем, что метод static
    if (!Modifier.isStatic(method.modifiers)) {
        println("Plugin function must be static")
        return null
    }

    // Тут ещё пара проверок, о которых я наверняка забыл

    return NamedFunction(name, method)
}

// Вспомогательная штука, чтобы выводить путь к файлу в нормальном виде.
val File.normalizedPath get() = Paths.get(this.path).toAbsolutePath().normalize().absolutePathString()
