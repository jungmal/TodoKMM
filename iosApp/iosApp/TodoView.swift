import SwiftUI
import shared
import Foundation
import Combine


struct TodoView: View {
    @StateObject var viewModel = TodoViewModel()

    var body: some View {
        VStack {
            Text("완료 후 삭제된 TODO 개수: \(viewModel.state.deletedCount)").padding(4)
            Text("가장 최근에 삭제된 TODO : \(viewModel.state.lastDeletedItem?.title ?? "없음")")
            
            HStack {
                TextField("enter TODO Title", text: Binding(
                    get: { viewModel.state.titleText },
                    set: { viewModel.updateTitleText($0) }
                ))
            }.padding(10)
            
            HStack {
                TextField("enter TODO imageUrl", text: Binding(
                    get: { viewModel.state.imageUrlText },
                    set: { viewModel.updateImageUrlText($0) }
                ))
            }.padding(10)
            
            Button(action: {
                viewModel.trigger(.addItem(title: viewModel.state.titleText, imageUrl: viewModel.state.imageUrlText))
            }) {
                HStack {
                    Spacer()
                    Text("ADD")
                        .foregroundColor(.white)
                    Spacer()
                }
                .padding()
                .contentShape(Rectangle())
            }
            .background(Color.blue)
            .cornerRadius(10)
            .frame(maxWidth: .infinity)
            .padding(.horizontal, 10)

            List(viewModel.state.itemList, id: \.self) { item in
                ToDoRow(item: item, actionDelete: {
                    viewModel.trigger(.deleteItem(item: item))
                }, updateToggle: {
                    viewModel.trigger(.updateItem(item: item, checked: !item.isFinish))
                })
            }
            .listStyle(PlainListStyle())
        }
        .onAppear {
            viewModel.trigger(.loadAllData)
        }
    }
}

struct ToDoRow: View {
    let item: TODOItem
    let actionDelete: () -> Void
    let updateToggle: () -> Void

    var body: some View {
        HStack {
            Text(item.title)
            Spacer()
            TransactionCardRow(transaction: Transaction(), imageUrl: item.imageUrl)
            Image(systemName: item.isFinish ? "checkmark.square.fill" : "square")
                .foregroundColor(item.isFinish ? Color(UIColor.systemBlue) : Color.secondary)
                .onTapGesture {
                    updateToggle()
                }
            Button(action: actionDelete) {
                Image(systemName: "trash")
                    .foregroundColor(.red)
            }
            .buttonStyle(PlainButtonStyle()) // 버튼 스타일을 명시적으로 지정하여 탭 영역을 제한
        }
        .contentShape(Rectangle()) // 전체 HStack에 대한 탭 가능 영역 확장
        .onTapGesture {
            updateToggle()
        }
    }
}

struct TodoView_Previews: PreviewProvider {
    static var previews: some View {
        TodoView()
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
            .frame(width:90, height:60)
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
