package main

import (
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
)

type dhex struct {
	date string `json:"date"`
	num string `json:"num"`
	publicKeyArray string `json:"publicKeyArray"`
}

func (t *dhex) Init (stub shim.ChaincodeStubInterface) pb.Response {
	return shim.Success(nil)
}

func (t *dhex) Invoke (stub shim.ChaincodeStubInterface) pb.Response {
	funcName,args := stub.GetFunctionAndParameters()
	if (funcName=="save") {
		return t.savePublicKey(stub,args)
	} else if(funcName=="query"){
		return t.queryPublicKey(stub,args)
	} else {
		return shim.Error("No such function!")
	}
}

func (t *dhex) savePublicKey(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if(len(args)!=2){
		return shim.Error("Execpt two args!")
	}else {
		err := stub.PutState(args[0],[]byte(args[1]))
		if(err!=nil){
			return shim.Error(err.Error())
		}
		return shim.Success([]byte("Save success!"))
	}
}

func (t *dhex) queryPublicKey(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if (len(args)!=1) {
		return shim.Error("Except on arg!")
	}else {
		value,err := stub.GetState(args[0])
		if (err!=nil) {
			shim.Error("No data found!")
		}
		return shim.Success(value)
	}
}

func main()  {
	err := shim.Start(new(dhex))
	if(err!=nil){
		fmt.Println("Dhex chaincode start errror!")
	}
}


