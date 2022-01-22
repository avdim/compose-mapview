import SwiftUI
import shared

func greet() -> String {
    return Greeting().greeting()
}

struct ContentView: View {
    var body: some View {
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

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
