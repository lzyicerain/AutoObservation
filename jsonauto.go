package main

import (
	"encoding/json"
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	"math"
	"strconv"
)


type jsonauto struct {
}

type Target struct {
	Longitude string `json:"longitude"`
	Latitude string `json:"latitude"`
}


func (t *jsonauto) Init (stub shim.ChaincodeStubInterface) pb.Response {
	return shim.Success([]byte("Init success!"))
}

func (t *jsonauto) Invoke(stub shim.ChaincodeStubInterface) pb.Response{
	funcName,args := stub.GetFunctionAndParameters()
	if(funcName=="loadTarget"){
		return t.loadTarget(stub,args)
	}else if(funcName=="queryTarget"){
		return t.queryTarget(stub,args)
	}else{
		return shim.Error("No such function!")
	}
}

func (t *jsonauto) loadTarget(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if(len(args) != 2){
		return shim.Error("Except two args!")
	}else{
		var tar Target
		err := json.Unmarshal([]byte(args[1]),&tar)
		if err != nil{
			fmt.Println("Error was %v",err)
		}
		fmt.Printf("%+v\n",tar)
		err2 := stub.PutState(args[0],[]byte(args[1]))
		if(err2 != nil) {
			return shim.Error(err2.Error())
		}
		return shim.Success([]byte("Load target Data success!"))
	}

}

func (t *jsonauto) queryTarget(stub shim.ChaincodeStubInterface, args []string) pb.Response{

	if(len(args)!=1){
		return shim.Error("Except one arg!")
	}else{
		value,err :=stub.GetState("Target")
		if(err != nil){
			shim.Error("No data found!")
		}
		//目标地点位置解析
		var tar Target
		err2 := json.Unmarshal(value,&tar)
		if err2 != nil{
			fmt.Println("Error2 was %v",err)
		}
		fmt.Printf("%+v\n",tar)
		t_lon,err := strconv.ParseFloat(tar.Longitude,32)
		t_lat,err := strconv.ParseFloat(tar.Latitude,32)
		//卫星位置解析
		var sat Target
		err3 := json.Unmarshal([]byte(args[0]),&sat)
		err4 := stub.PutState("Sat",[]byte(args[0]))
		if (err3 != nil || err4!= nil){
			fmt.Println("Error3 was %v",err)
		}
		fmt.Printf("%+v\n",tar)
		s_lon,err := strconv.ParseFloat(sat.Longitude,32)
		s_lat,err := strconv.ParseFloat(sat.Latitude,32)




		//str1 := strconv.FormatFloat(math.Abs(t_lon-s_lon), 'e', 5, 64)
		//str2 := strconv.FormatFloat(math.Abs(t_lat-s_lat), 'e', 5, 64)



		if (math.Abs(t_lon-s_lon)<=20 && math.Abs(t_lat-s_lat)<=20){
			return shim.Success([]byte("Change Sensor!"))
		}else {
			return shim.Success([]byte("Keep unchanged!"))
		}
	}
}

func main(){
	err:=shim.Start(new(jsonauto))
	if(err!=nil){
		fmt.Println("Jsonauto chaincode start error!")
	}
}