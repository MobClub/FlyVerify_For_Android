package cn.fly.verify.demo;


public abstract class ResultListener<T> {
	public abstract void onComplete(T data);
	public abstract void onFailure(Throwable e);
}
