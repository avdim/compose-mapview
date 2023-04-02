Please first read OpenStreetMap policy: https://operations.osmfoundation.org/policies/tiles/

# MapView
Composable UI component for Desktop, Android and Browser

## Setup
In local.properties, add keys:
```
sdk.dir=Path to Android SDK
```

## Usage
```Kotlin
MapView(
    modifier = Modifier.fillMaxSize(),
    latitude =  60.00,
    longitude = 29.75,
    startScale = 500.0,
    onMapViewClick = { latitude, longitude ->
        println("Hello, Geo coordinates, (lat $latitude, lon $longitude)")
        true
    }
)
```

## Run samples targets
```./gradlew sample-desktop:run```  
```./gradlew sample-android:installDebug #(connect device first)```  
```./gradlew sample-browser:jsBrowserRun```  

## Describe sources
Проект разбит на несколько includeBuild, чтобы нормально проходил импорт в Idea.
Состоит из следующих подпроектов:

### include-mapview
Тут лежит мультиплатформенный MapView с общей логикой инициализации.    

### include-ui-android-desktop
Общий UI код между Android и Desktop. Всё максимально вынесено в commonMain sourceSet    
MapViewAndroidDesktop.kt - отрисовка на Canvas и обработка pointer ввода.

### include-ui-browser
MapViewBrowser.kt - отрисовка в html `<canvas>`

### include-io-android-desktop
Общий код между Android и Desktop для запросов в сеть и кэширования на диск.  
Для простоты этот модуль подключен как plugins { kotlin("jvm") } 

### include-model
Мультиплатформенная логика.  
В этом модуле нет зависимости на Compose и в будующем можно переиспользовать для iOS (например в SwiftUI)

### include-tile-image
Мультиплатформенная картинка TileImage.

### include-config
Содержит конфиги для настройки и отладки

### unit-tests
Чтобы хорошо работала отладка в Idea - все тесты находятся в этом модуле в commonTest и jvmTest sourceSet-ах

### Ещё модули
sample-android, sample-browser, sample-desktop - приложения, которые можно запустить

