class Producer implements Runnable{
  private final Queue<Integer>shared;
  private final int MAX_SIZE;

  public Producer(Queue<Integer>shared,int MAX_SIZE){
    this.shared=new LinkedList<>();
    this.MAX_SIZE=MAX_SIZE;
  }
  @Override
  public void run(){
    int counter=0;
    while(true){
      try{
        produce(counter++);
      }
      catch(InterruptedException ex){
        ex.printStackTrace();
      }
    }
  }
  private void produce(int i) throws InterruptedExcpetion{
    synchronized(sharedQueue){
      while(sharedQueue.size()==MAX_SIZE){
        System.out.println("Queue is full Producer is waiting.....");
        sharedQueue.wait();
      }
      System.out.println("produced: "+i);
      sharedQueue.add(i);
      sharedQueue.notifyAll();
    }
  }
}

class Consumer implements Runnable{
  private final Queue<Integer>sharedQueue;

  public Consumer(Queue<Integer>sharedQueue){
    this.sharedQueue=sharedQueue;
  }

  @Override
  public void run(){
    while(true){
      try{
        consume();
      }
      catch(InterruptedException ex){
        ex.printStackTrace();
      }
    }
  }

  public void consume() throws InturreptedException{
    synchronized(sharedQueue){
      while(sharedQueue.isEmpty()){
          System.out.println("Queue is empty,consumer is waiting....");
          sharedQueue.wait();
      }
      int item= sharedQueue.poll();
      System.out.println("Consumed: " + item);
      sharedQueue.notifyAll();
    }
  }
}

public class ProducerAndConsumerProblem{

  public static void main(String[] args){
    Queue<Integer>sharedQueue=LinkedList<>();
    int MAX_SIZE=5;
    Thread producerThread=new Thread(new Producer(sharedQueue,MAX_SIZE),"Producer");
    Thread consumerThread=new Thread(new Consumer(sharedQueue),"Consumer");

    producerThread.start();
    consumerThread.start();
  }
}

