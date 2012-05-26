/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\In Progress\\Java\\Android\\Jobmine-Android\\src\\com\\jobmine\\service\\JobmineInterface.aidl
 */
package com.jobmine.service;
public interface JobmineInterface extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.jobmine.service.JobmineInterface
{
private static final java.lang.String DESCRIPTOR = "com.jobmine.service.JobmineInterface";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.jobmine.service.JobmineInterface interface,
 * generating a proxy if needed.
 */
public static com.jobmine.service.JobmineInterface asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.jobmine.service.JobmineInterface))) {
return ((com.jobmine.service.JobmineInterface)iin);
}
return new com.jobmine.service.JobmineInterface.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_go:
{
data.enforceInterface(DESCRIPTOR);
this.go();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.jobmine.service.JobmineInterface
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public void go() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_go, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_go = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void go() throws android.os.RemoteException;
}
