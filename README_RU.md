![promo](https://raw.githubusercontent.com/AlmasB/git-server/master/storage/images/fxgl_promo.jpg)

## Языки

&emsp;&emsp;[English](https://github.com/AlmasB/FXGL/blob/dev/README.md)

&emsp;&emsp;[简体中文](https://github.com/AlmasB/FXGL/blob/dev/README_CN.md)

&emsp;&emsp;Русский

## О фреймворке

<img src="https://raw.githubusercontent.com/AlmasB/git-server/master/storage/images/fxgl_logo.png" width="128" />

Фреймворк для разработки игр на основе JavaFX

[![Maven Central](https://img.shields.io/maven-central/v/com.github.almasb/fxgl.svg)]()
[![Build Status](https://github.com/AlmasB/FXGL/workflows/Java%20CI%20with%20Maven/badge.svg)](https://github.com/AlmasB/FXGL/actions)
[![codecov](https://codecov.io/gh/AlmasB/FXGL/branch/dev/graph/badge.svg)](https://codecov.io/gh/AlmasB/FXGL)
[![sponsor](https://img.shields.io/badge/sponsor-%241-brightgreen)](https://github.com/sponsors/AlmasB)
[![JFXCentral](https://img.shields.io/badge/Find_me_on-JFXCentral-blue?logo=googlechrome&logoColor=white)](https://www.jfx-central.com/libraries/fxgl)

### Почему FXGL?

* Не требуется установка или настройка
* "Из коробки": Java 8-21, Win/Mac/Linux/Android 8+/iOS 11.0+/Web
* Простой и чистый API, более высокого уровня по сравнению с другими движками
* Расширение JavaFX: нет необходимости изучать новый API пользовательского интерфейса
* Реальные методы разработки игр: Entity-Component, интерполированная анимация, частицы и [многое другое](https://github.com/AlmasB/FXGL/wiki/Core-Features)
* Игры легко упаковываются в один исполняемый файл .jar или нативные образы

### Хорошо подходит для:

* Любой 2D игры (сайд-скроллер/платформер/аркада/РПГ)
* Любого бизнес-приложения со сложными элементами управления/анимацией пользовательского интерфейса
* Экспериментального 3D
* Хобби/академических/коммерческих проектов
* Преподавания/обучения/совершенствования навыков разработки игр
* Быстрого прототипирования идей приложений

### Пример кода

```java
public class BasicGameApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Basic Game App");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
```

## Начало работы

Для быстрого погружения в код, ознакомьтесь с [базовыми примерами](https://github.com/AlmasB/FXGL/tree/dev/fxgl-samples/src/main/java/basics).

Или смотрите:

* [Книга по FXGL](https://link.springer.com/book/10.1007/978-1-4842-8625-8?sap-outbound-id=3352BB472E8E602B4B29844F1A86CCC4374DDF6E)
* [Видеообзор возможностей](https://youtu.be/fuDQg7W0v4g)
* [Использование FXGL с IntelliJ](https://youtu.be/LhmlFC6KE2Q)
* [Вики и написанные руководства](https://github.com/AlmasB/FXGL/wiki)
* [Руководства на YouTube](https://www.youtube.com/playlist?list=PL4h6ypqTi3RTiTuAQFKE6xwflnPKyFuPp)
* [Демонстрационный код](fxgl-samples)
* [Игры на FXGL](https://github.com/AlmasB/FXGLGames) (с исходным кодом)
* [Опубликованные демо](https://fxgl.itch.io/) на itch.io

### Maven

* Шаблон проекта [Maven](https://github.com/AlmasB/FXGL-MavenGradle), если вы используете Java и/или Kotlin

```xml
<dependency>
    <groupId>com.github.almasb</groupId>
    <artifactId>fxgl</artifactId>
    <version>21.1</version>
</dependency>
```

### Gradle

* Шаблон проекта [Gradle](https://github.com/AlmasB/FXGL-MavenGradle), если вы используете Java и/или Kotlin

Пожалуйста, обратитесь к шаблону в случае возникновения ошибок.

```gradle
repositories {
    jcenter()
}

dependencies {
    compile 'com.github.almasb:fxgl:21.1'
}
```

### Modularity

Если вы хотите разработать модульное приложение, вот полный пример вашего `module-info.java`:

```java
open module app.name {
    requires com.almasb.fxgl.all;
}
```

### Uber jar

Скачайте последний uber jar из раздела [Релизы](https://github.com/AlmasB/FXGL/releases)

## Сообщество

* Университет Брайтона, Великобритания
* Университет Ноттингема, Великобритания
* Технологический институт Джорджии, США
* kidOYO, США
* Средняя школа Уолтона, США
* Средняя школа Конанта, США
* Институт бизнеса и технологий Зеландии, Дания
* Федеральный институт образования, науки и технологий Рио-Гранде-ду-Сул, Бразилия
* Школа инженерии / информатики FHNW, Швейцария
* Гимназия Иоганна-Андреаса-Шмеллера, Наббург, Германия

Если ваше учебное заведение хочет использовать FXGL или уже использует его, оставьте заметку в [чате](https://github.com/AlmasB/FXGL/discussions), чтобы мы могли внести его в список.

Руководства сообщества:

- [Space Ranger](https://www.journaldev.com/40219/space-rangers-game-java-fxgl) на journaldev
- [Geometry Wars](https://webtechie.be/post/2020-05-07-getting-started-with-fxgl/) на webtechie
- [Mazela-Man](https://dykstrom.github.io/mazela-man-web/home/) от dykstrom

Проекты сообщества (помеченные тегом `fxgl`): 

- [SOFTKNK.IO](https://github.com/softknk/softknk.io)
- [Consume](https://ergoscrit.itch.io/consume)
- [FXGL Sliding Puzzle](https://github.com/beryx/fxgl-sliding-puzzle)

Если вы хотите, чтобы ваш проект был представлен здесь, просто добавьте заметку в [чате](https://github.com/AlmasB/FXGL/discussions).

### Команда разработчиков

Описание ролей представлено в [Руководстве по внесению вклада](CONTRIBUTING.md).

Поддерживающие (Соавторы):

* [Almas Baimagambetov](https://github.com/AlmasB)

Координаторы:

* [Chengen Zhao](https://github.com/chengenzhao)

Тестировщики:

* [Carl Dea](https://github.com/carldea)
* [Frank Delporte](https://github.com/FDelporte)

### Внесение вклада и поддержка

Если вы хотите собрать FXGL из исходного кода или внести свой вклад,
пожалуйста, ознакомьтесь с [Руководством по внесению вклада](CONTRIBUTING.md) (включая не-кодовые аспекты).
FXGL полностью модульный, поэтому новым участникам не обязательно понимать весь код, только тот модуль, в которой делается вклад.
Внесенные изменения будут рассмотрены в соответствии с [Кодексом поведения](CODE_OF_CONDUCT.md).

Вы можете поддержать разработку FXGL или показать интерес, просто добавив репозиторий в избранное или став [спонсором](https://github.com/sponsors/AlmasB).

### Спонсоры

Пользователи:

* @Marsl10
* @SergeMerzliakov
* @mbains
* @sabit86
* @hendrikebbers
* @ImperaEtConquer
* @thejeed
* @chikega

Компании:

* @karakun

### Обратная связь

* Задавайте вопросы через [Обсуждения GitHub](https://github.com/AlmasB/FXGL/discussions) 
* Задавайте вопросы на [StackOverflow](https://stackoverflow.com/search?q=fxgl) с тегами `javafx` и `fxgl`
* Пишите твит с хештегом [#fxgl](https://twitter.com/search?src=typd&q=%23fxgl)
