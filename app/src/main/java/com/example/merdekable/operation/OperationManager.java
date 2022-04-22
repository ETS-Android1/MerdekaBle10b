package com.example.merdekable.operation;

import java.util.LinkedList;
import java.util.Queue;

/**
 * OperationManager class which will be used by the scanner and advertiser to operate their
 * operations sequentially. It contains a queue to make the operations work in a sequence.
 */
public class OperationManager {

    private Queue<Operation> operations = new LinkedList<>();
    private Operation currentOp = null;

    public OperationManager(){

    }


    /**
     * This method is to request operation to be execute.
     * @param operation operation
     */
    public synchronized void request(Operation operation){
        operations.add(operation);
        if(currentOp == null){
            currentOp = operations.poll();
            currentOp.performOperation();

        }
    }

    /**
     * To tell the Manager the operation has completed. The Manager will dequeue the next operation to execute.
     */
    public synchronized void operationCompleted(){
        currentOp = null;
        if(operations.peek() != null){
            currentOp = operations.poll();
            currentOp.performOperation();
        }
    }
}
