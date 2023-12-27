# Пример Pluggable приложения

## Основная часть

... лежит в Mainframe -- там есть два варианта.
Main -- с честной проверкой всех возможных возникающих ошибок, и MainLite -- без неё.
MainLite проще для восприятия, но в реальном проекте, конечно, надо писать как в Main.

Программа ищет плагины в папке, которая указана в `pluginRoot`. 
Умеет читать отдельные классфайлы или jar'ы целиком. 
Если это jar, то она читает в манифесте ключ Function-Class и ищет там функцию статическую function(short), возвращающую short.
Если это classfile, то ищет функцию сразу в нём.
После того, как все "функции" загружены, она сообщает о том, сколько их и какие они, и пытается применить к единице до тех пор, пока получаются разные результаты.

Например, если есть единственная функция:

f(x) = x / 2, если x чётное

f(x) = 3x + 1, если нечётное

То программа пройдёт по циклу f(1) = 4, f(2) = 1, и завершится, выведя \[1, 2, 4]

Так как различных Short'ов всего 65536, программа когда-нибудь кончится :)

## Плагин Single (SingleClassFile)

Это одиночный классфайл, содержащий единственную функцию -- описанную выше. Больше и не скажешь.

## Плагин Plugin (SingleClassFileInsideJar)

Это jar, внутри которого единственный классфайл org.ldemetrios.interior.Plugin, он реализует умножение на два.

## Плагин ComplexPlugin

Это jar, сгенерированный автоматически системой сборки Maven из проекта. 
Помимо манифеста там много мусора, ну да ладно.
Этот плагин имитирует бурную деятельность, то есть какие-то якобы сложные вычисления, растянутые на множество файлов. 
Обратите внимание, что исходных .kt файлов было два, а после компиляции стало 4 классфайла. 
Это связано с тем, что классфайлы, очевидно, более примитивная единица информации.

## Структура папок

### BuiltPlugins

Здесь лежат плагины, так сказать, в состоянии релиза. Это .class и .jar файлы. Отсюда их читает Mainframe.

### PluginSources

Здесь то, из чего эти плагины были сгенерированы (проект Complex Plugin, а так же единичные .java файлы, которые компилировались вручную)

### PluginSourcesDecompiled

Так как jar -- это просто zip, я в этой папке сложил то, что на самом деле оказалось внутри. 
Не ожидается, что вы умеете читать классфайлы, поэтому они были заранее декомпилированы в java (там несложно, текста только много).
Эта папка отражает PluginSources, только вместо .class здесь .java, а вместо .jar -- просто папки.
