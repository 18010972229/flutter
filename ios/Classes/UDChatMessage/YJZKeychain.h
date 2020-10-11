//
//  YJZKeychain.h
//  YJZ
//
//  Created by 陈福杰 on 2020/7/7.
//  Copyright © 2020 月交子. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface YJZKeychain : NSObject

+ (void)saveServiceKey:(NSString *)service data:(id)data;

+ (id)loadServiceKey:(NSString *)service;

+ (void)deleteServiceKey:(NSString *)service;

@end

NS_ASSUME_NONNULL_END
