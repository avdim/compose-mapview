import SwiftUI
import shared
import model
import config

public struct MapViewSwiftUI: View {
    let mviStore: MapStoreWrapper

    @ObservedObject var myViewModel: MapViewModel

    public init() {
        mviStore = MapStoreWrapper(
                sideEffectHandler: { (store, sideEffect) in
                    print("sideEffect: \(sideEffect)")
                    let effect = SwiftHelpersKt.sideEffectAsLoadTile(effect: sideEffect)
                    if (effect != nil) {
                        let tile = effect!.tile
                        let url = SwiftHelpersKt.createTileUrl(tile: tile)
                        print("loadTileImage \(url)")
                        DispatchQueue.global().async {
                            if let data = try? Data(contentsOf: URL(string: url)!) {
                                if let image = UIImage(data: data) {
                                    DispatchQueue.main.async {
                                        store.send(
                                                intent: SwiftHelpersKt.createIntentTileLoaded(
                                                        tile: tile,
                                                        imageIos: image
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
        )
        self.myViewModel = MapViewModel(mviStore)
    }

    public var body: some View {
        if #available(iOS 15.0, *) {
            Canvas { (context: inout GraphicsContext, size: CGSize) in
                for displayTile in myViewModel.myState.displayTiles {
                    guard let img = displayTile.image else {
                        continue
                    }
                    let t = displayTile.displayTile
                    let platformImage = SwiftHelpersKt.extract(tileImage: img)
                    let image: Image = Image(uiImage: platformImage)

                    let rect3 = CGRect(
                            origin: CGPoint(x: Int(t.x), y: Int(t.y)),
                            size: CGSize(width: Int(t.size), height: Int(t.size))
                    )

                    context.draw(image, in: rect3)
                }
            }
                    .gesture(
                            DragGesture(minimumDistance: 5, coordinateSpace: .global)
                                    .onChanged { value in
                                        let dx = value.location.x - value.startLocation.x
                                        let dy = value.location.y - value.startLocation.y
                                        mviStore.sendIntent(intent: SwiftHelpersKt.createIntentMove(x: Int32(dx), y: Int32(dy)))
                                    }
                    )
                    .frame(width: 400, height: 400)
                    //                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .border(Color.blue)
        } else {
            // Fallback on earlier versions
        }
    }
}
