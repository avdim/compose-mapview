import SwiftUI
import ioshelper
import model
import config

var previousDragPos: CGPoint? = nil
var previousMagnitude: CGFloat? = nil
let IOS_SCALE = 2.0

public struct MapViewSwiftUI: View {
    let mviStore: MapStoreWrapper

    @ObservedObject var mapViewModel: MapViewModel

    public init() {
        mviStore = MapStoreWrapper { (store, sideEffect) in
            let effect = SwiftHelpersKt.sideEffectAsLoadTile(effect: sideEffect)
            if (effect != nil) {
                let tile = effect!.tile
                let url = SwiftHelpersKt.createTileUrl(tile: tile)
                DispatchQueue.global().async {
                    if let data = try? Data(contentsOf: URL(string: url)!) {
                        if let image = UIImage(data: data) {
                            DispatchQueue.main.async {
                                store.send(intent: SwiftHelpersKt.createIntentTileLoaded(
                                        tile: tile,
                                        imageIos: image
                                ))
                            }
                        }
                    }
                }
            }
        }
        self.mapViewModel = MapViewModel(mviStore)
    }

    public var body: some View {
        if #available(iOS 15.0, *) {
            Canvas { (context: inout GraphicsContext, size: CGSize) in
                mviStore.sendIntent(intent: SwiftHelpersKt.createIntentSetSize(
                        width: Int32(size.width * IOS_SCALE),
                        height: Int32(size.height * IOS_SCALE)
                ))
                for displayTile in mapViewModel.myState.displayTiles {
                    guard let img = displayTile.image else {
                        continue
                    }
                    let t = displayTile.displayTile
                    let platformImage = SwiftHelpersKt.extract(tileImage: img)
                    let image: Image = Image(uiImage: platformImage)
                    let rect = CGRect(
                            origin: CGPoint(x: Double(t.x) / IOS_SCALE, y: Double(t.y) / IOS_SCALE),
                            size: CGSize(width: Double(t.size) / IOS_SCALE, height: Double(t.size) / IOS_SCALE)
                    )
                    context.draw(image, in: rect)
                }
            }.gesture(
                DragGesture(minimumDistance: 2, coordinateSpace: .global)
                .onChanged { value in
                    if (previousDragPos != nil) {
                        let prev: CGPoint = previousDragPos!
                        let dx = prev.x - value.location.x
                        let dy = prev.y - value.location.y
                        mviStore.sendIntent(intent: SwiftHelpersKt.createIntentMove(
                                x: Int32(dx),
                                y: Int32(dy)
                        ))
                    }
                    previousDragPos = value.location
                }
                .onEnded { _ in
                    previousDragPos = nil
                }
            )
            .gesture(
                TapGesture()
                .onEnded { _ in
                    mviStore.sendIntent(intent: SwiftHelpersKt.createIntentZoom(
                            x: Int32(mapViewModel.myState.width / 2),
                            y: Int32(mapViewModel.myState.height / 2),
                            delta: 0.5
                    ))
                }
            )
            .gesture(
                MagnificationGesture()
                .onChanged { value in
                    if (previousMagnitude != nil) {
                        let prev = previousMagnitude!
                        mviStore.sendIntent(intent: SwiftHelpersKt.createIntentZoom(
                                x: Int32(mapViewModel.myState.width / 2),
                                y: Int32(mapViewModel.myState.height / 2),
                                delta: Float(value.magnitude - prev)
                        ))
                    }
                    previousMagnitude = value.magnitude
                }
                .onEnded { _ in
                    previousMagnitude = nil
                }
            )
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        } else {
            Text("need iOS 15.0+")
        }
    }
}
