import SwiftUI
import shared
import model

public struct MapViewSwiftUI: View {
    let mviStore: MapStoreWrapper

    @ObservedObject var myViewModel: MapViewModel

    public init() {
        mviStore = MapStoreWrapper(
                sideEffectHandler: { (sideEffect) in
                    print("sideEffect: \(sideEffect)")
                    let effect = SwiftHelpersKt.sideEffectAsLoadTile(effect: sideEffect)
                    if(effect != nil) {
                        print("load tile \(effect!.tile)")
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

                let rect3 = CGRect(origin: .zero, size: size).insetBy(dx: 10, dy: 10)
                let image = Image("800x800")

                context.draw(image, in: rect3)
            }
                    .frame(width: 300, height: 200)
                    .border(Color.blue)
        } else {
            // Fallback on earlier versions
        }
    }
}
