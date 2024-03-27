### 网关服务

```
 [{
	"id": "dvs-admin",
	"order": 0,
	"predicates": [{
		"args": {
			"pattern": "/dvs-admin/**"
		},
		"name": "Path"
	}],
	"uri": "lb://dvs-admin",
	"filters": [{
		"name": "StripPrefix",
		"args": {
			"parts": 1
		}
	}]
}, {
	"id": "dvs-manage",
	"order": 1,
	"predicates": [{
		"args": {
			"pattern": "/dvs-manage/**"
		},
		"name": "Path"
	}],
	"uri": "lb://dvs-manage",
	"filters": [{
		"name": "StripPrefix",
		"args": {
			"parts": 1
		}
	}]
}]
```
```
StripPrefix:请求发送到下游前从请求中剥离的路径个数
parts:剥离的路径的个数 
例如:lb://dvs-mmanage服务配置parts为1，调用时把pattern前缀为dvs-manage去除
```