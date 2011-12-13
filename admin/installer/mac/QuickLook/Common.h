#import <Cocoa/Cocoa.h>

#define DEBUG

#ifdef DEBUG
#define frlog(...) NSLog(__VA_ARGS__)
#else
#define frlog(...)
#endif

NSString* createTempDir();