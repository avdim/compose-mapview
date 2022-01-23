import Foundation
import ioshelper
import model
import tileimage

public class MapViewModel: ObservableObject {
    @Published public var myState: Include_modelMapState<Include_tile_imageTileImage>

    public init(_ mviStore: MapStoreAdapter) {
        myState = mviStore.getLastState()
        mviStore.addListener(listener: { state in
            self.myState = state
        })
    }
}
