//
//  ContentViewEvent.swift
//  iosApp
//
//  Created by HYUNGCHAN JUNG on 2023/12/29.
//  Copyright © 2023 orgName. All rights reserved.
//

import shared
import Foundation

enum TodoViewEvent {
    case addItem(title: String, imageUrl: String)
    case deleteItem(item: TODOItem)
    case updateItem(item: TODOItem, checked: Bool)
    case loadAllData
}
