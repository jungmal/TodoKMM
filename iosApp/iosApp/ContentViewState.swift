//
//  ContentViewState.swift
//  iosApp
//
//  Created by HYUNGCHAN JUNG on 2023/12/29.
//  Copyright © 2023 orgName. All rights reserved.
//

import shared
import Foundation


struct ContentViewState {
    var itemList: [TODOItem] = []
    var lastDeletedItem: DeletedTODOItem? = nil
    var deletedCount: Int = 0
    var titleText: String = ""
    var imageUrlText: String = ""
}
