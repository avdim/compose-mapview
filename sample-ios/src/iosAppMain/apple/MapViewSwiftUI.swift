import SwiftUI
import shared
import model

public struct MapViewSwiftUI: View {
    let mviStore: MapStoreWrapper

    @ObservedObject var myViewModel: MapViewModel

    public init() {
        mviStore = MapStoreWrapper(
                sideEffectHandler: { (sideEffect) in
//                    switch sideEffect {
//                    case let openOrder as MapSideEffect.LoadTile:
                        print("todo load tile")
//                    default:
//                        print("do nothing")
//                    }
                }
        )
        self.myViewModel = MapViewModel(mviStore)
    }

    public var body: some View {
        Text("todo MapView Canvas iOS")
    }
}
