'''
수정해야 하는 config file list

---------------------------------------
scouter/agent.host/conf/scouter.conf
# Server IP(default:127.0.0.1)
net_collector_ip=

#Port Setting(defalut:6100)
net_collector_udp_port=
net_coleector_tcp_port=
---------------------------------------
---------------------------------------
scouter/agent.java/conf/scouter.conf
# Server IP(default:127.0.0.1)
net_collector_ip=

#Port Setting(defalut:6100)
net_collector_udp_port=m
net_coleector_tcp_port=
# scouter Name(defalt : tomcat)
obj_name=
______________________________________
______________________________________
scouter/server/conf/scouter.conf

#database Dir default = ./databse
db_dir=

#log Dir default = ./logs
log_dir=

#udp port default =6100
net_udp_listen_port=

#tcp port default = 6100
net_tcp_listen_port=

#line configure
ext_plugin_line_send_alert=line meassage 발송여부 default = false
ext_plugin_line_debug= loging 여부 default = false
ext_plugin_line_level= 수신 레벨 (0: info, 1:warn , 2:error, 3:fatal) default =0
ext_plugin_line_access_token= line channel token
ext_plugin_line_group_id= chatting room id (line의 webhook 호출시 request 에서 확인 가능)

-------------------------------------
'''

def sayYesOrNo(conf):
    message = conf +"의 설정을 하겠습니까?(y/n)" 
    keyin =input(message)
    if(keyin=='y'):
        return True
    else : return False

def confHost():
    #실제 경로  : scouter/agent.host/conf/scouter.conf
    message ="""
# Server IP(default:127.0.0.1)
net_collector_ip=

#Port Setting(defalut:6100)
net_collector_udp_port=
net_coleector_tcp_port=
"""
    print(message)
    
    
    with open("./agent.host/conf/scouter.conf","w+") as f:
        keyin=input("Server가 사용할 ip주소를 입력하시오.(빈칸일 시 default 값이 입력 됩니다.)")
        if keyin == "" :
            keyin="127.0.0.1"
        collectorIP= "net_collector_ip="+keyin+"\n"
        f.writelines(collectorIP)
        keyin=input("Server가 사용할 udp port NUM을입력하시오.(빈칸일 시 default 값이 입력 됩니다.)")
        if keyin == "" :
            keyin="6100"
        collector_udp_port="net_collector_udp_port="+keyin+"\n"
        f.writelines(collector_udp_port)
        keyin=input("Server가 사용할 tcp port NUM을입력하시오.(빈칸일 시 default 값이 입력 됩니다.)")
        if keyin == "" :
            keyin="6100"
        collector_tcp_port="net_collector_tcp_port="+keyin+"\n"
        f.writelines(collector_tcp_port)
def confAgentJava():
    #실제 경로  : scouter/agent.java/conf/scouter.conf
    message ="""
# Server IP(default:127.0.0.1)
net_collector_ip=

#Port Setting(defalut:6100)
net_collector_udp_port=
net_coleector_tcp_port=
# scouter Name(defalt : tomcat)
obj_name=
"""
    print(message)        
    with open("./agent.java/conf/scouter.conf","w+") as f:
        keyin=input("Server가 사용할 ip주소를 입력하시오.(빈칸일 시 default 값이 입력 됩니다.)")
        if keyin == "" :
            keyin="127.0.0.1"
        collectorIP= "net_collector_ip="+keyin+"\n"
        f.writelines(collectorIP)
        keyin=input("Server가 사용할 udp port NUM을입력하시오.(빈칸일 시 default 값이 입력 됩니다.)")
        if keyin == "" :
            keyin="6100"
        collector_udp_port="net_collector_udp_port="+keyin+"\n"
        f.writelines(collector_udp_port)
        keyin=input("Server가 사용할 tcp port NUM을입력하시오.(빈칸일 시 default 값이 입력 됩니다.)")
        if keyin == "" :
            keyin="6100"
        collector_tcp_port="net_collector_tcp_port="+keyin+"\n"
        f.writelines(collector_tcp_port)
        keyin=input("scouter name을 입력하시오.(빈칸일 시 default 값이 입력 됩니다.)")
        if keyin == "" :
            keyin="tomcat"
        obj_name="obj_name="+keyin+"\n"
        f.writelines(obj_name)
        
 
def confServer():
    #실제 경로  : scouter/server/conf/scouter.conf
    message ="""
#database Dir default = ./databse
db_dir=

#log Dir default = ./logs
log_dir=

#udp port default =6100
net_udp_listen_port=

#tcp port default = 6100
net_tcp_listen_port=

#line configure
ext_plugin_line_send_alert=line meassage 발송여부 default = false
ext_plugin_line_debug= loging 여부 default = false
ext_plugin_line_level= 수신 레벨 (0: info, 1:warn , 2:error, 3:fatal) default =0
ext_plugin_line_access_token= line channel token
ext_plugin_line_group_id= chatting room id (line의 webhook 호출시 request 에서 확인 가능)

"""
    print(message)        
    with open("./server/conf/scouter.conf","w+") as f:
        keyin=input("Server가 저장할 database의 위치를 입력하시오.(빈칸일 시 default 값이 입력 됩니다.)")
        if keyin == "" :
            keyin="./databse"
        db_dir= "db_dir="+keyin+"\n"
        f.writelines(db_dir)

        keyin=input("Server가 저장할 로그의 위치를 입력하시오.(빈칸일 시 default 값이 입력 됩니다.)")
        if keyin == "" :
            keyin="./log"
        dir_log="log_dir="+keyin+"\n"
        f.writelines(dir_log)

        keyin=input("Server가 사용할 udp port NUM을입력하시오.(빈칸일 시 default 값이 입력 됩니다.)")
        if keyin == "" :
            keyin="6100"
        udp_port="net_collector_udp_port="+keyin+"\n"
        f.writelines(udp_port)

        keyin=input("Server가 사용할 tcp port NUM을입력하시오.(빈칸일 시 default 값이 입력 됩니다.)")
        if keyin == "" :
            keyin="6100"
        collector_tcp_port="net_collector_tcp_port="+keyin+"\n"
        f.writelines(collector_tcp_port)

        keyin=input("Line Message 기능 활성화 기능 여부를 입력하시오(true/false).(빈칸일 시 default 값이 입력 됩니다.)")
        if keyin == "" :
            keyin="false"
        ext_plugin_line_send_alert="ext_plugin_line_send_alert="+keyin+"\n"
        f.writelines(ext_plugin_line_send_alert)

        keyin=input("Line module debuging 여부를 입력하시오(true/false).(빈칸일 시 default 값이 입력 됩니다.)")
        if keyin == "" :
            keyin="false"
        ext_plugin_line_debug="ext_plugin_line_debug="+keyin+"\n"
        f.writelines(ext_plugin_line_debug)

        keyin=input("line 모듈의 수신 level 활성화 여부를 입력하시오.(빈칸일 시 default 값이 입력 됩니다.)")
        if keyin == "" :
            keyin="0"
        ext_plugin_line_level="ext_plugin_line_level="+keyin+"\n"
        f.writelines(ext_plugin_line_level)

        keyin=input("message를 보내는 access token 을 입력하시오.")
        ext_plugin_line_access_token="ext_plugin_line_access_token="+keyin+"\n"
        f.writelines(ext_plugin_line_access_token)

        keyin=input("message를 받는 그룹 ID를 입력하시오.")
        ext_plugin_line_group_id="ext_plugin_line_group_id="+keyin+"\n"
        f.writelines(ext_plugin_line_group_id)

        
if sayYesOrNo("host"):
    confHost()
if sayYesOrNo("agent java"):
    confAgentJava()
if sayYesOrNo("Server"):
    confServer()


