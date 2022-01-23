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
                context.stroke(
                        Path(ellipseIn: CGRect(origin: .zero, size: size)),
                        with: .color(.green),
                        lineWidth: 4)

                for displayTile in myViewModel.myState.displayTiles {
                    guard let img = displayTile.image else { continue }
                    let platformImage = SwiftHelpersKt.extract(tileImage: img)
                    let image: Image = Image(uiImage: platformImage)

                    let rect3 = CGRect(origin: .zero, size: size).insetBy(dx: 10, dy: 10)

                    context.draw(image, in: rect3)
                }
            }
                    .frame(width: 400, height: 400)
//                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .border(Color.blue)
        } else {
            // Fallback on earlier versions
        }
    }
}
