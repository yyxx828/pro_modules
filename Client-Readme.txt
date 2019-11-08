1、首先将fileClient源码直接复制到业务项目中
2、将包名修改准确
3、项目要使用文件服务,需要进行一次性注册,之后则可以使用
4、需要在[application-dev.properties]中增加以下参数,对应生产环境也需要加上

#文件服务系统配置参数
fileService.ip=http://192.168.1.151:8021
#文件服务器注册时候但systemId,不区分大小写
fileService.account=busiSystemId
#文件服务器注册时候密码,区分大小写
fileService.password=passwords
