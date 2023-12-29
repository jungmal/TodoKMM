//
//  ContentViewModel.swift
//  iosApp
//
//  Created by HYUNGCHAN JUNG on 2023/12/29.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared
import Foundation
import Combine


class ContentViewModel: ObservableObject {
    @Published private(set) var state = ContentViewState()
    let appDataBase: AppDataBase = AppDataBase(driverFactory: DriverFactory())

    func trigger(_ event: ContentViewEvent) {
        switch event {
        case .addItem(let title, let imageUrl):
            addItem(title: title, imageUrl: imageUrl)
        case .deleteItem(let item):
            deleteItem(item: item)
        case .updateItem(let item, let checked):
            updateItem(item: item, checked: checked)
        case .loadAllData:
            loadAllData()
        }
    }
    
    
    func updateTitleText(_ text: String) {
        state.titleText = text
    }

    func updateImageUrlText(_ text: String) {
        state.imageUrlText = text
    }


    private func addItem(title: String, imageUrl: String) {
        appDataBase.insertItem(title: title, imageUrl: imageUrl)
        loadAllData()
    }

    private func deleteItem(item: TODOItem) {
        let currentTimeMillis = Int64(Date().timeIntervalSince1970 * 1000)
        appDataBase.deleteItem(
            id: item.id,
            title: item.title,
            imageUrl: item.imageUrl,
            checked: item.isFinish,
            time: currentTimeMillis
        )
        loadAllData()
    }

    private func updateItem(item: TODOItem, checked: Bool) {
        appDataBase.updateCheck(checked: checked, id: item.id)
        loadAllData()
    }

    private func loadAllData() {
        appDataBase.getAllItemFlow().collect(collector: Collector<[TODOItem]> {value in
            DispatchQueue.main.async {
                self.state.itemList = value
            }
        }) { error in
            print(error ?? "")
        }
        appDataBase.getFinishedItemCountFlow().collect(collector: Collector<Int> {value in
            DispatchQueue.main.async {
                self.state.deletedCount = value
            }
        }) { error in
            print(error ?? "")
        }
        appDataBase.getLatestDeletedItemFlow().collect(collector: Collector<DeletedTODOItem> {value in
            DispatchQueue.main.async {
                self.state.lastDeletedItem = value
            }
        }) { error in
            print(error ?? "")
        }
    }

    init() {
        trigger(.loadAllData)
    }
}
