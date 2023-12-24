import SwiftUI
import shared
import Foundation
import SwiftUI
import Combine


struct ContentView: View {
    let appDataBase : AppDataBase = AppDataBase(driverFactory: DriverFactory())
    
    @State var itemList : [TODOItem] = []
    @State var titleText = ""
    @State var imageUrlText = ""
    
    var body: some View {
        VStack{
            HStack {
                TextField("enter TODO Title", text: $titleText)
            }.padding(10)
            
            HStack {
                TextField("enter TODO imageUrl", text: $imageUrlText)
            
            }.padding(10)
            
            
            Button("ADD") {
                appDataBase.insertItem(title: titleText, imageUrl: imageUrlText)
            }
            
            
            ForEach(itemList,id:\.self) { item in
                ToDoRow(item: item) {
                    appDataBase.deleteItem(id: item.id)
                } updateToggle: {
                    appDataBase.updateCheck(checked: !item.isFinish, id: item.id)
                }
            }
            Spacer()
        }.onAppear {
            appDataBase.getAllItemFlow().collect(collector: Collector<[TODOItem]> {value in
                self.itemList = value
            }) { error in
                print(error ?? "")
            }
        }
    }
}

struct ToDoRow: View {
    let item : TODOItem
    let actionDelete : () -> Void
    let updateToggle : () -> Void
    var body: some View {
        HStack{
            Text(item.title + item.imageUrl)
            Spacer()
            TransactionCardRow(transaction: Transaction(), imageUrl: item.imageUrl)
            Spacer()
            Image(systemName: item.isFinish ? "checkmark.square.fill" : "square")
                .foregroundColor(item.isFinish ? Color(UIColor.systemBlue) : Color.secondary)
                .onTapGesture {
                    updateToggle()
                }
            Button("Delete") {
                actionDelete()
            }
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}

struct TransactionCardRow: View {
    var transaction: Transaction
    var imageUrl: String

    var body: some View {
        CustomImageView(urlString: imageUrl)
    }
}

struct CustomImageView: View {
    var urlString: String
    @ObservedObject var imageLoader = ImageLoaderService()
    @State var image: UIImage = UIImage()
    
    var body: some View {
        Image(uiImage: image)
            .resizable()
            .aspectRatio(contentMode: .fit)
            .frame(width:100, height:100)
            .onReceive(imageLoader.$image) { image in
                self.image = image
            }
            .onAppear {
                imageLoader.loadImage(for: urlString)
            }
    }
}

class ImageLoaderService: ObservableObject {
    @Published var image: UIImage = UIImage()
    
    func loadImage(for urlString: String) {
        guard let url = URL(string: urlString) else { return }
        
        let task = URLSession.shared.dataTask(with: url) { data, response, error in
            guard let data = data else { return }
            DispatchQueue.main.async {
                self.image = UIImage(data: data) ?? UIImage()
            }
        }
        task.resume()
    }
    
}
