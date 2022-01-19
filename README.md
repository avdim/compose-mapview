# MapView
Composable UI component for Desktop, Android and Browser

## Setup
In local.properties, add keys:
```
mapTilerSecretKey=xXxXxXxXxXxXx
sdk.dir=Path to Android SDK
```
Where **mapTilerSecretKey** is secret API key for cloud.maptiler.com  
Here you can get this key: https://cloud.maptiler.com/maps/streets/ (register and look at url field `?key=...#`)

## Usage
```Kotlin
MapView(
    modifier = Modifier.fillMaxSize(),
    mapTilerSecretKey = MAPTILER_SECRET_KEY,
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
Главный мультиплатформенный модуль, тут лежит мультиплатформенный MapView() с общей логикой инициализации. 
А также платформо специфичные PlatformMapView. 
