package com.echoman.storage;

public interface Storable {
	
	public static final Storable IAMLATER = new Storable(){

		@Override
		public String getUid() {
			return null;
		}

		@Override
		public Object[] toArray() {
			return null;
		}

		@Override
		public Object[] equalValues() {
			return null;
		}};
	
	public String getUid();

	public Object[] toArray();
	
	public Object[] equalValues();
}
