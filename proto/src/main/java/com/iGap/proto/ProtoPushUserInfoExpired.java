// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: PushUserInfoExpired.proto

package com.iGap.proto;

public final class ProtoPushUserInfoExpired {
    private ProtoPushUserInfoExpired() {
    }

    public static void registerAllExtensions(com.google.protobuf.ExtensionRegistryLite registry) {
    }

    public static void registerAllExtensions(com.google.protobuf.ExtensionRegistry registry) {
        registerAllExtensions((com.google.protobuf.ExtensionRegistryLite) registry);
    }

    public interface PushUserInfoExpiredResponseOrBuilder extends
            // @@protoc_insertion_point(interface_extends:proto.PushUserInfoExpiredResponse)
            com.google.protobuf.MessageOrBuilder {

        /**
         * <code>optional .proto.Response response = 1;</code>
         */
        boolean hasResponse();

        /**
         * <code>optional .proto.Response response = 1;</code>
         */
        ProtoResponse.Response getResponse();

        /**
         * <code>optional .proto.Response response = 1;</code>
         */
        ProtoResponse.ResponseOrBuilder getResponseOrBuilder();

        /**
         * <code>optional uint64 user_id = 2;</code>
         */
        long getUserId();
    }

    /**
     * Protobuf type {@code proto.PushUserInfoExpiredResponse}
     */
    public static final class PushUserInfoExpiredResponse extends com.google.protobuf.GeneratedMessageV3 implements
            // @@protoc_insertion_point(message_implements:proto.PushUserInfoExpiredResponse)
            PushUserInfoExpiredResponseOrBuilder {
        // Use PushUserInfoExpiredResponse.newBuilder() to construct.
        private PushUserInfoExpiredResponse(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }

        private PushUserInfoExpiredResponse() {
            userId_ = 0L;
        }

        @Override
        public final com.google.protobuf.UnknownFieldSet getUnknownFields() {
            return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
        }

        private PushUserInfoExpiredResponse(com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
            this();
            int mutable_bitField0_ = 0;
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        default: {
                            if (!input.skipField(tag)) {
                                done = true;
                            }
                            break;
                        }
                        case 10: {
                            ProtoResponse.Response.Builder subBuilder = null;
                            if (response_ != null) {
                                subBuilder = response_.toBuilder();
                            }
                            response_ = input.readMessage(ProtoResponse.Response.parser(), extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(response_);
                                response_ = subBuilder.buildPartial();
                            }

                            break;
                        }
                        case 16: {

                            userId_ = input.readUInt64();
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(e).setUnfinishedMessage(this);
            } finally {
                makeExtensionsImmutable();
            }
        }

        public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
            return ProtoPushUserInfoExpired.internal_static_proto_PushUserInfoExpiredResponse_descriptor;
        }

        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return ProtoPushUserInfoExpired.internal_static_proto_PushUserInfoExpiredResponse_fieldAccessorTable.ensureFieldAccessorsInitialized(PushUserInfoExpiredResponse.class, Builder.class);
        }

        public static final int RESPONSE_FIELD_NUMBER = 1;
        private ProtoResponse.Response response_;

        /**
         * <code>optional .proto.Response response = 1;</code>
         */
        public boolean hasResponse() {
            return response_ != null;
        }

        /**
         * <code>optional .proto.Response response = 1;</code>
         */
        public ProtoResponse.Response getResponse() {
            return response_ == null ? ProtoResponse.Response.getDefaultInstance() : response_;
        }

        /**
         * <code>optional .proto.Response response = 1;</code>
         */
        public ProtoResponse.ResponseOrBuilder getResponseOrBuilder() {
            return getResponse();
        }

        public static final int USER_ID_FIELD_NUMBER = 2;
        private long userId_;

        /**
         * <code>optional uint64 user_id = 2;</code>
         */
        public long getUserId() {
            return userId_;
        }

        private byte memoizedIsInitialized = -1;

        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1) return true;
            if (isInitialized == 0) return false;

            memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(com.google.protobuf.CodedOutputStream output) throws java.io.IOException {
            if (response_ != null) {
                output.writeMessage(1, getResponse());
            }
            if (userId_ != 0L) {
                output.writeUInt64(2, userId_);
            }
        }

        public int getSerializedSize() {
            int size = memoizedSize;
            if (size != -1) return size;

            size = 0;
            if (response_ != null) {
                size += com.google.protobuf.CodedOutputStream.computeMessageSize(1, getResponse());
            }
            if (userId_ != 0L) {
                size += com.google.protobuf.CodedOutputStream.computeUInt64Size(2, userId_);
            }
            memoizedSize = size;
            return size;
        }

        private static final long serialVersionUID = 0L;

        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof PushUserInfoExpiredResponse)) {
                return super.equals(obj);
            }
            PushUserInfoExpiredResponse other = (PushUserInfoExpiredResponse) obj;

            boolean result = true;
            result = result && (hasResponse() == other.hasResponse());
            if (hasResponse()) {
                result = result && getResponse().equals(other.getResponse());
            }
            result = result && (getUserId() == other.getUserId());
            return result;
        }

        @Override
        public int hashCode() {
            if (memoizedHashCode != 0) {
                return memoizedHashCode;
            }
            int hash = 41;
            hash = (19 * hash) + getDescriptorForType().hashCode();
            if (hasResponse()) {
                hash = (37 * hash) + RESPONSE_FIELD_NUMBER;
                hash = (53 * hash) + getResponse().hashCode();
            }
            hash = (37 * hash) + USER_ID_FIELD_NUMBER;
            hash = (53 * hash) + com.google.protobuf.Internal.hashLong(getUserId());
            hash = (29 * hash) + unknownFields.hashCode();
            memoizedHashCode = hash;
            return hash;
        }

        public static PushUserInfoExpiredResponse parseFrom(com.google.protobuf.ByteString data) throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static PushUserInfoExpiredResponse parseFrom(com.google.protobuf.ByteString data, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static PushUserInfoExpiredResponse parseFrom(byte[] data) throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static PushUserInfoExpiredResponse parseFrom(byte[] data, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static PushUserInfoExpiredResponse parseFrom(java.io.InputStream input) throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static PushUserInfoExpiredResponse parseFrom(java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static PushUserInfoExpiredResponse parseDelimitedFrom(java.io.InputStream input) throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
        }

        public static PushUserInfoExpiredResponse parseDelimitedFrom(java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static PushUserInfoExpiredResponse parseFrom(com.google.protobuf.CodedInputStream input) throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static PushUserInfoExpiredResponse parseFrom(com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(PushUserInfoExpiredResponse prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
        }

        @Override
        protected Builder newBuilderForType(BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }

        /**
         * Protobuf type {@code proto.PushUserInfoExpiredResponse}
         */
        public static final class Builder extends com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
                // @@protoc_insertion_point(builder_implements:proto.PushUserInfoExpiredResponse)
                PushUserInfoExpiredResponseOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
                return ProtoPushUserInfoExpired.internal_static_proto_PushUserInfoExpiredResponse_descriptor;
            }

            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return ProtoPushUserInfoExpired.internal_static_proto_PushUserInfoExpiredResponse_fieldAccessorTable.ensureFieldAccessorsInitialized(PushUserInfoExpiredResponse.class, Builder.class);
            }

            // Construct using com.iGap.proto.ProtoPushUserInfoExpired.PushUserInfoExpiredResponse.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders) {
                }
            }

            public Builder clear() {
                super.clear();
                if (responseBuilder_ == null) {
                    response_ = null;
                } else {
                    response_ = null;
                    responseBuilder_ = null;
                }
                userId_ = 0L;

                return this;
            }

            public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
                return ProtoPushUserInfoExpired.internal_static_proto_PushUserInfoExpiredResponse_descriptor;
            }

            public PushUserInfoExpiredResponse getDefaultInstanceForType() {
                return PushUserInfoExpiredResponse.getDefaultInstance();
            }

            public PushUserInfoExpiredResponse build() {
                PushUserInfoExpiredResponse result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public PushUserInfoExpiredResponse buildPartial() {
                PushUserInfoExpiredResponse result = new PushUserInfoExpiredResponse(this);
                if (responseBuilder_ == null) {
                    result.response_ = response_;
                } else {
                    result.response_ = responseBuilder_.build();
                }
                result.userId_ = userId_;
                onBuilt();
                return result;
            }

            public Builder clone() {
                return (Builder) super.clone();
            }

            public Builder setField(com.google.protobuf.Descriptors.FieldDescriptor field, Object value) {
                return (Builder) super.setField(field, value);
            }

            public Builder clearField(com.google.protobuf.Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }

            public Builder clearOneof(com.google.protobuf.Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }

            public Builder setRepeatedField(com.google.protobuf.Descriptors.FieldDescriptor field, int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }

            public Builder addRepeatedField(com.google.protobuf.Descriptors.FieldDescriptor field, Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }

            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof PushUserInfoExpiredResponse) {
                    return mergeFrom((PushUserInfoExpiredResponse) other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(PushUserInfoExpiredResponse other) {
                if (other == PushUserInfoExpiredResponse.getDefaultInstance()) return this;
                if (other.hasResponse()) {
                    mergeResponse(other.getResponse());
                }
                if (other.getUserId() != 0L) {
                    setUserId(other.getUserId());
                }
                onChanged();
                return this;
            }

            public final boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
                PushUserInfoExpiredResponse parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (PushUserInfoExpiredResponse) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            private ProtoResponse.Response response_ = null;
            private com.google.protobuf.SingleFieldBuilderV3<ProtoResponse.Response, ProtoResponse.Response.Builder, ProtoResponse.ResponseOrBuilder> responseBuilder_;

            /**
             * <code>optional .proto.Response response = 1;</code>
             */
            public boolean hasResponse() {
                return responseBuilder_ != null || response_ != null;
            }

            /**
             * <code>optional .proto.Response response = 1;</code>
             */
            public ProtoResponse.Response getResponse() {
                if (responseBuilder_ == null) {
                    return response_ == null ? ProtoResponse.Response.getDefaultInstance() : response_;
                } else {
                    return responseBuilder_.getMessage();
                }
            }

            /**
             * <code>optional .proto.Response response = 1;</code>
             */
            public Builder setResponse(ProtoResponse.Response value) {
                if (responseBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    response_ = value;
                    onChanged();
                } else {
                    responseBuilder_.setMessage(value);
                }

                return this;
            }

            /**
             * <code>optional .proto.Response response = 1;</code>
             */
            public Builder setResponse(ProtoResponse.Response.Builder builderForValue) {
                if (responseBuilder_ == null) {
                    response_ = builderForValue.build();
                    onChanged();
                } else {
                    responseBuilder_.setMessage(builderForValue.build());
                }

                return this;
            }

            /**
             * <code>optional .proto.Response response = 1;</code>
             */
            public Builder mergeResponse(ProtoResponse.Response value) {
                if (responseBuilder_ == null) {
                    if (response_ != null) {
                        response_ = ProtoResponse.Response.newBuilder(response_).mergeFrom(value).buildPartial();
                    } else {
                        response_ = value;
                    }
                    onChanged();
                } else {
                    responseBuilder_.mergeFrom(value);
                }

                return this;
            }

            /**
             * <code>optional .proto.Response response = 1;</code>
             */
            public Builder clearResponse() {
                if (responseBuilder_ == null) {
                    response_ = null;
                    onChanged();
                } else {
                    response_ = null;
                    responseBuilder_ = null;
                }

                return this;
            }

            /**
             * <code>optional .proto.Response response = 1;</code>
             */
            public ProtoResponse.Response.Builder getResponseBuilder() {

                onChanged();
                return getResponseFieldBuilder().getBuilder();
            }

            /**
             * <code>optional .proto.Response response = 1;</code>
             */
            public ProtoResponse.ResponseOrBuilder getResponseOrBuilder() {
                if (responseBuilder_ != null) {
                    return responseBuilder_.getMessageOrBuilder();
                } else {
                    return response_ == null ? ProtoResponse.Response.getDefaultInstance() : response_;
                }
            }

            /**
             * <code>optional .proto.Response response = 1;</code>
             */
            private com.google.protobuf.SingleFieldBuilderV3<ProtoResponse.Response, ProtoResponse.Response.Builder, ProtoResponse.ResponseOrBuilder> getResponseFieldBuilder() {
                if (responseBuilder_ == null) {
                    responseBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<ProtoResponse.Response, ProtoResponse.Response.Builder, ProtoResponse.ResponseOrBuilder>(getResponse(), getParentForChildren(), isClean());
                    response_ = null;
                }
                return responseBuilder_;
            }

            private long userId_;

            /**
             * <code>optional uint64 user_id = 2;</code>
             */
            public long getUserId() {
                return userId_;
            }

            /**
             * <code>optional uint64 user_id = 2;</code>
             */
            public Builder setUserId(long value) {

                userId_ = value;
                onChanged();
                return this;
            }

            /**
             * <code>optional uint64 user_id = 2;</code>
             */
            public Builder clearUserId() {

                userId_ = 0L;
                onChanged();
                return this;
            }

            public final Builder setUnknownFields(final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }

            public final Builder mergeUnknownFields(final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }


            // @@protoc_insertion_point(builder_scope:proto.PushUserInfoExpiredResponse)
        }

        // @@protoc_insertion_point(class_scope:proto.PushUserInfoExpiredResponse)
        private static final PushUserInfoExpiredResponse DEFAULT_INSTANCE;

        static {
            DEFAULT_INSTANCE = new PushUserInfoExpiredResponse();
        }

        public static PushUserInfoExpiredResponse getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<PushUserInfoExpiredResponse> PARSER = new com.google.protobuf.AbstractParser<PushUserInfoExpiredResponse>() {
            public PushUserInfoExpiredResponse parsePartialFrom(com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
                return new PushUserInfoExpiredResponse(input, extensionRegistry);
            }
        };

        public static com.google.protobuf.Parser<PushUserInfoExpiredResponse> parser() {
            return PARSER;
        }

        @Override
        public com.google.protobuf.Parser<PushUserInfoExpiredResponse> getParserForType() {
            return PARSER;
        }

        public PushUserInfoExpiredResponse getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

    }

    private static final com.google.protobuf.Descriptors.Descriptor internal_static_proto_PushUserInfoExpiredResponse_descriptor;
    private static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internal_static_proto_PushUserInfoExpiredResponse_fieldAccessorTable;

    public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor() {
        return descriptor;
    }

    private static com.google.protobuf.Descriptors.FileDescriptor descriptor;

    static {
        String[] descriptorData = {
                "\n\031PushUserInfoExpired.proto\022\005proto\032\016Resp" +
                        "onse.proto\"Q\n\033PushUserInfoExpiredRespons" +
                        "e\022!\n\010response\030\001 \001(\0132\017.proto.Response\022\017\n\007" +
                        "user_id\030\002 \001(\004B*\n\016com.iGap.protoB\030ProtoPu" +
                        "shUserInfoExpiredb\006proto3"
        };
        com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
            public com.google.protobuf.ExtensionRegistry assignDescriptors(com.google.protobuf.Descriptors.FileDescriptor root) {
                descriptor = root;
                return null;
            }
        };
        com.google.protobuf.Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new com.google.protobuf.Descriptors.FileDescriptor[]{
                ProtoResponse.getDescriptor(),
        }, assigner);
        internal_static_proto_PushUserInfoExpiredResponse_descriptor = getDescriptor().getMessageTypes().get(0);
        internal_static_proto_PushUserInfoExpiredResponse_fieldAccessorTable = new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(internal_static_proto_PushUserInfoExpiredResponse_descriptor, new String[]{"Response", "UserId",});
        ProtoResponse.getDescriptor();
    }

    // @@protoc_insertion_point(outer_class_scope)
}
