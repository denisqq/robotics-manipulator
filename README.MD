###Механотронные системы и робототехника 


####Task
<pre>
«2D Engine»
Цель – закрепление знаний по теме механика многозвенных систем. Практическое применение знаний по линейной алгебре, аналитической геометрии, теории комплексной переменной, программирований, компьютерным информационным системам. Улучшение навыков командной работы.
Студенты должны самостоятельно разбиться на малые группы от 2 до 4-х человек и распределить обязанности по выполнению работы. Работа предоставляется в виде ссылки на публичный репозиторий (например, github). Оценка зависит от вклада каждого из участников группы в проект. Работа в одиночку не допускается.

Задача
Создать приложение с графическим интерфейсом, которое позволяет создавать, редактивроать, анализировать и анимировать многозвенные системы, состоящие из твёрдых сегментов и сочленений.

Минимальные требования
Каждый объект (сегмент или сустав) обладает своей локальной С.К. и  центром масс, который можно посмотреть или скрыть на экране. Каждый объект обладает весом (который задаётся при создании в определённых границах). Могут существовать невесомые объекты и объекты с отрицательным весом. Эти параметры можно менять у уже созданного объекта.
Сегменты могут быть невидимыми (не отрисовываются) или эфемерными (могут накладываться друг на друга). Или и теми, и другими. Эти свойства можно менять у уже созданных объектов. При изменении свойства «эфемерный» необходима проверка на самопересечение.
Первый объект (сегмент или сустав) привязывается к центральной точке на плоскости под заданным углом. Остальные строятся относительно него.
Сегмент дополнительно обладает размером (с фиксированной малой шириной), который может изменяться во время работы из интерфейса. Изменение размера ограничено от минимального до максимального (заданного разработчиками).
Сустав может обладать ограничением по углу поворота.
Новый объект создаётся относительно конца родителя, с начальными характеристиками, которые могут быть заданы пользователем.
Программа должна не допускать самопересечения или сигнализировать об этом (например, меняя цвет пересекающихся объектов).
Объекты отрисовываются простыми геометрическими объектами (например, прямоугольник и окружность)
Программа должна позволять вычислить центр масс всей системы и отобразить его.
Изменение таких параметров, как поворот, изменение длины звеньев и изменение массы (с последующим изменением центра масс) должно быть анимировано.
Должна быть возможность удаления объекта, с автоматическим удалением всех его наследников.
Должна быть возможность сохранения дерева в файл и его подгрузки.
Рабочее поле должно вмещать всё дерево, автоматически изменяя размер, если дерево выходит за пределы рабочего поля.
Работа с объектами с помощью указателя мыши, а не форм ввода. Возможность выделения объекта кликом по нему, а не перебором из списка. 

Требования к подсистеме моделирования
Библиотека должна использовать матричную алгебру, либо работу с комплексными числами для реализации операций трансляции и поворота

Дополнительные требования
Необязательными, но интересными, были бы следующие возможности
- Привязка изображения к объекту
- Вычисление момента относительно заданного вектора силы тяжести
- Наивная реализация инверсной кинематики, при малых перемещениях вершины объекта
</pre>

###Group members

- Denis Tretyakov(@denisqq)
- Egor Gladkih(@GladkihEgor)
- Evegeniya Vyatkina(@VyatkinaEvgenia)

###Requirements
- Oracle HotSpot Java 1.8 with JavaFX