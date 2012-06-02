/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\Christopher\\workspace\\Jobmine-Android\\src\\com\\jobmine\\service\\JobmineInterface.aidl
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
case TRANSACTION_getApplications:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
boolean _result = this.getApplications(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getInterviews:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
boolean _result = this.getInterviews(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getJobDescription:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.getJobDescription(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_checkForUpdates:
{
data.enforceInterface(DESCRIPTOR);
this.checkForUpdates();
reply.writeNoException();
return true;
}
case TRANSACTION_getLastNetworkError:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getLastNetworkError();
reply.writeNoException();
reply.writeInt(_result);
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
public boolean getApplications(boolean forceUpdate) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((forceUpdate)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_getApplications, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean getInterviews(boolean forceUpdate) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((forceUpdate)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_getInterviews, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean getJobDescription(java.lang.String jobId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(jobId);
mRemote.transact(Stub.TRANSACTION_getJobDescription, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void checkForUpdates() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_checkForUpdates, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public int getLastNetworkError() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getLastNetworkError, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_getApplications = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getInterviews = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_getJobDescription = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_checkForUpdates = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_getLastNetworkError = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
}
public boolean getApplications(boolean forceUpdate) throws android.os.RemoteException;
public boolean getInterviews(boolean forceUpdate) throws android.os.RemoteException;
public boolean getJobDescription(java.lang.String jobId) throws android.os.RemoteException;
public void checkForUpdates() throws android.os.RemoteException;
public int getLastNetworkError() throws android.os.RemoteException;
}
