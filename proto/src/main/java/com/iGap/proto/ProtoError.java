// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: Error.proto

package com.iGap.proto;

public final class ProtoError {
    private static final com.google.protobuf.Descriptors.Descriptor
        internal_static_proto_ErrorResponse_descriptor;
    private static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internal_static_proto_ErrorResponse_fieldAccessorTable;
    private static com.google.protobuf.Descriptors.FileDescriptor descriptor;

    static {
        java.lang.String[] descriptorData = {
            "\n\013Error.proto\022\005proto\032\016Response.proto\"h\n\r" +
                "ErrorResponse\022!\n\010response\030\001 \001(\0132\017.proto." +
                "Response\022\022\n\nmajor_code\030\002 \001(\r\022\022\n\nminor_co" +
                "de\030\003 \001(\r\022\014\n\004wait\030\004 \001(\rB\034\n\016com.iGap.proto" +
                "B\nProtoErrorb\006proto3"
        };
        com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
            new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
                public com.google.protobuf.ExtensionRegistry assignDescriptors(
                    com.google.protobuf.Descriptors.FileDescriptor root) {
                    descriptor = root;
                    return null;
                }
            };
        com.google.protobuf.Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(
            descriptorData, new com.google.protobuf.Descriptors.FileDescriptor[] {
                com.iGap.proto.ProtoResponse.getDescriptor(),
            }, assigner);
        internal_static_proto_ErrorResponse_descriptor = getDescriptor().getMessageTypes().get(0);
        internal_static_proto_ErrorResponse_fieldAccessorTable =
            new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_proto_ErrorResponse_descriptor,
                new java.lang.String[] { "Response", "MajorCode", "MinorCode", "Wait", });
        com.iGap.proto.ProtoResponse.getDescriptor();
    }

    private ProtoError() {
    }

    public static void registerAllExtensions(com.google.protobuf.ExtensionRegistryLite registry) {
    }

    public static void registerAllExtensions(com.google.protobuf.ExtensionRegistry registry) {
        registerAllExtensions((com.google.protobuf.ExtensionRegistryLite) registry);
    }

    public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor() {
        return descriptor;
    }

    public interface ErrorResponseOrBuilder extends
        // @@protoc_insertion_point(interface_extends:proto.ErrorResponse)
        com.google.protobuf.MessageOrBuilder {

        /**
         * <code>optional .proto.Response response = 1;</code>
         */
        boolean hasResponse();

        /**
         * <code>optional .proto.Response response = 1;</code>
         */
        com.iGap.proto.ProtoResponse.Response getResponse();

        /**
         * <code>optional .proto.Response response = 1;</code>
         */
        com.iGap.proto.ProtoResponse.ResponseOrBuilder getResponseOrBuilder();

        /**
         * <code>optional uint32 major_code = 2;</code>
         */
        int getMajorCode();

        /**
         * <code>optional uint32 minor_code = 3;</code>
         */
        int getMinorCode();

        /**
         * <code>optional uint32 wait = 4;</code>
         */
        int getWait();
    }

    /**
     * Protobuf type {@code proto.ErrorResponse}
     */
    public static final class ErrorResponse extends com.google.protobuf.GeneratedMessageV3
        implements
        // @@protoc_insertion_point(message_implements:proto.ErrorResponse)
        ErrorResponseOrBuilder {
        public static final int RESPONSE_FIELD_NUMBER = 1;
        public static final int MAJOR_CODE_FIELD_NUMBER = 2;
        public static final int MINOR_CODE_FIELD_NUMBER = 3;
        public static final int WAIT_FIELD_NUMBER = 4;
        private static final long serialVersionUID = 0L;
        // @@protoc_insertion_point(class_scope:proto.ErrorResponse)
        private static final com.iGap.proto.ProtoError.ErrorResponse DEFAULT_INSTANCE;
        private static final com.google.protobuf.Parser<ErrorResponse> PARSER =
            new com.google.protobuf.AbstractParser<ErrorResponse>() {
                public ErrorResponse parsePartialFrom(com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                    return new ErrorResponse(input, extensionRegistry);
                }
            };

        static {
            DEFAULT_INSTANCE = new com.iGap.proto.ProtoError.ErrorResponse();
        }

        private com.iGap.proto.ProtoResponse.Response response_;
        private int majorCode_;
        private int minorCode_;
        private int wait_;
        private byte memoizedIsInitialized = -1;

        // Use ErrorResponse.newBuilder() to construct.
        private ErrorResponse(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }

        private ErrorResponse() {
            majorCode_ = 0;
            minorCode_ = 0;
            wait_ = 0;
        }

        private ErrorResponse(com.google.protobuf.CodedInputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
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
                            com.iGap.proto.ProtoResponse.Response.Builder subBuilder = null;
                            if (response_ != null) {
                                subBuilder = response_.toBuilder();
                            }
                            response_ =
                                input.readMessage(com.iGap.proto.ProtoResponse.Response.parser(),
                                    extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(response_);
                                response_ = subBuilder.buildPartial();
                            }

                            break;
                        }
                        case 16: {

                            majorCode_ = input.readUInt32();
                            break;
                        }
                        case 24: {

                            minorCode_ = input.readUInt32();
                            break;
                        }
                        case 32: {

                            wait_ = input.readUInt32();
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(
                    e).setUnfinishedMessage(this);
            } finally {
                makeExtensionsImmutable();
            }
        }

        public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
            return com.iGap.proto.ProtoError.internal_static_proto_ErrorResponse_descriptor;
        }

        public static com.iGap.proto.ProtoError.ErrorResponse parseFrom(
            com.google.protobuf.ByteString data)
            throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static com.iGap.proto.ProtoError.ErrorResponse parseFrom(
            com.google.protobuf.ByteString data,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static com.iGap.proto.ProtoError.ErrorResponse parseFrom(byte[] data)
            throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static com.iGap.proto.ProtoError.ErrorResponse parseFrom(byte[] data,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static com.iGap.proto.ProtoError.ErrorResponse parseFrom(java.io.InputStream input)
            throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static com.iGap.proto.ProtoError.ErrorResponse parseFrom(java.io.InputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input,
                extensionRegistry);
        }

        public static com.iGap.proto.ProtoError.ErrorResponse parseDelimitedFrom(
            java.io.InputStream input) throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER,
                input);
        }

        public static com.iGap.proto.ProtoError.ErrorResponse parseDelimitedFrom(
            java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER,
                input, extensionRegistry);
        }

        public static com.iGap.proto.ProtoError.ErrorResponse parseFrom(
            com.google.protobuf.CodedInputStream input) throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static com.iGap.proto.ProtoError.ErrorResponse parseFrom(
            com.google.protobuf.CodedInputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input,
                extensionRegistry);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(com.iGap.proto.ProtoError.ErrorResponse prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        public static com.iGap.proto.ProtoError.ErrorResponse getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static com.google.protobuf.Parser<ErrorResponse> parser() {
            return PARSER;
        }

        @java.lang.Override public final com.google.protobuf.UnknownFieldSet getUnknownFields() {
            return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
        }

        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return com.iGap.proto.ProtoError.internal_static_proto_ErrorResponse_fieldAccessorTable.ensureFieldAccessorsInitialized(
                com.iGap.proto.ProtoError.ErrorResponse.class,
                com.iGap.proto.ProtoError.ErrorResponse.Builder.class);
        }

        /**
         * <code>optional .proto.Response response = 1;</code>
         */
        public boolean hasResponse() {
            return response_ != null;
        }

        /**
         * <code>optional .proto.Response response = 1;</code>
         */
        public com.iGap.proto.ProtoResponse.Response getResponse() {
            return response_ == null ? com.iGap.proto.ProtoResponse.Response.getDefaultInstance()
                : response_;
        }

        /**
         * <code>optional .proto.Response response = 1;</code>
         */
        public com.iGap.proto.ProtoResponse.ResponseOrBuilder getResponseOrBuilder() {
            return getResponse();
        }

        /**
         * <code>optional uint32 major_code = 2;</code>
         */
        public int getMajorCode() {
            return majorCode_;
        }

        /**
         * <code>optional uint32 minor_code = 3;</code>
         */
        public int getMinorCode() {
            return minorCode_;
        }

        /**
         * <code>optional uint32 wait = 4;</code>
         */
        public int getWait() {
            return wait_;
        }

        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1) return true;
            if (isInitialized == 0) return false;

            memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(com.google.protobuf.CodedOutputStream output)
            throws java.io.IOException {
            if (response_ != null) {
                output.writeMessage(1, getResponse());
            }
            if (majorCode_ != 0) {
                output.writeUInt32(2, majorCode_);
            }
            if (minorCode_ != 0) {
                output.writeUInt32(3, minorCode_);
            }
            if (wait_ != 0) {
                output.writeUInt32(4, wait_);
            }
        }

        public int getSerializedSize() {
            int size = memoizedSize;
            if (size != -1) return size;

            size = 0;
            if (response_ != null) {
                size += com.google.protobuf.CodedOutputStream.computeMessageSize(1, getResponse());
            }
            if (majorCode_ != 0) {
                size += com.google.protobuf.CodedOutputStream.computeUInt32Size(2, majorCode_);
            }
            if (minorCode_ != 0) {
                size += com.google.protobuf.CodedOutputStream.computeUInt32Size(3, minorCode_);
            }
            if (wait_ != 0) {
                size += com.google.protobuf.CodedOutputStream.computeUInt32Size(4, wait_);
            }
            memoizedSize = size;
            return size;
        }

        @java.lang.Override public boolean equals(final java.lang.Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof com.iGap.proto.ProtoError.ErrorResponse)) {
                return super.equals(obj);
            }
            com.iGap.proto.ProtoError.ErrorResponse other =
                (com.iGap.proto.ProtoError.ErrorResponse) obj;

            boolean result = true;
            result = result && (hasResponse() == other.hasResponse());
            if (hasResponse()) {
                result = result && getResponse().equals(other.getResponse());
            }
            result = result && (getMajorCode() == other.getMajorCode());
            result = result && (getMinorCode() == other.getMinorCode());
            result = result && (getWait() == other.getWait());
            return result;
        }

        @java.lang.Override public int hashCode() {
            if (memoizedHashCode != 0) {
                return memoizedHashCode;
            }
            int hash = 41;
            hash = (19 * hash) + getDescriptorForType().hashCode();
            if (hasResponse()) {
                hash = (37 * hash) + RESPONSE_FIELD_NUMBER;
                hash = (53 * hash) + getResponse().hashCode();
            }
            hash = (37 * hash) + MAJOR_CODE_FIELD_NUMBER;
            hash = (53 * hash) + getMajorCode();
            hash = (37 * hash) + MINOR_CODE_FIELD_NUMBER;
            hash = (53 * hash) + getMinorCode();
            hash = (37 * hash) + WAIT_FIELD_NUMBER;
            hash = (53 * hash) + getWait();
            hash = (29 * hash) + unknownFields.hashCode();
            memoizedHashCode = hash;
            return hash;
        }

        public Builder newBuilderForType() {
            return newBuilder();
        }

        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
        }

        @java.lang.Override protected Builder newBuilderForType(
            com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }

        @java.lang.Override public com.google.protobuf.Parser<ErrorResponse> getParserForType() {
            return PARSER;
        }

        public com.iGap.proto.ProtoError.ErrorResponse getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

        /**
         * Protobuf type {@code proto.ErrorResponse}
         */
        public static final class Builder
            extends com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
            // @@protoc_insertion_point(builder_implements:proto.ErrorResponse)
            com.iGap.proto.ProtoError.ErrorResponseOrBuilder {
            private com.iGap.proto.ProtoResponse.Response response_ = null;
            private com.google.protobuf.SingleFieldBuilderV3<com.iGap.proto.ProtoResponse.Response, com.iGap.proto.ProtoResponse.Response.Builder, com.iGap.proto.ProtoResponse.ResponseOrBuilder>
                responseBuilder_;
            private int majorCode_;
            private int minorCode_;
            private int wait_;

            // Construct using com.iGap.proto.ProtoError.ErrorResponse.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }

            public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
                return com.iGap.proto.ProtoError.internal_static_proto_ErrorResponse_descriptor;
            }

            protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
                return com.iGap.proto.ProtoError.internal_static_proto_ErrorResponse_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(com.iGap.proto.ProtoError.ErrorResponse.class,
                        com.iGap.proto.ProtoError.ErrorResponse.Builder.class);
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
                majorCode_ = 0;

                minorCode_ = 0;

                wait_ = 0;

                return this;
            }

            public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
                return com.iGap.proto.ProtoError.internal_static_proto_ErrorResponse_descriptor;
            }

            public com.iGap.proto.ProtoError.ErrorResponse getDefaultInstanceForType() {
                return com.iGap.proto.ProtoError.ErrorResponse.getDefaultInstance();
            }

            public com.iGap.proto.ProtoError.ErrorResponse build() {
                com.iGap.proto.ProtoError.ErrorResponse result = buildPartial();
                if (!result.isInitialized()) {
                    throw Builder.newUninitializedMessageException(result);
                }
                return result;
            }

            public com.iGap.proto.ProtoError.ErrorResponse buildPartial() {
                com.iGap.proto.ProtoError.ErrorResponse result =
                    new com.iGap.proto.ProtoError.ErrorResponse(this);
                if (responseBuilder_ == null) {
                    result.response_ = response_;
                } else {
                    result.response_ = responseBuilder_.build();
                }
                result.majorCode_ = majorCode_;
                result.minorCode_ = minorCode_;
                result.wait_ = wait_;
                onBuilt();
                return result;
            }

            public Builder clone() {
                return (Builder) super.clone();
            }

            public Builder setField(com.google.protobuf.Descriptors.FieldDescriptor field,
                Object value) {
                return (Builder) super.setField(field, value);
            }

            public Builder clearField(com.google.protobuf.Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }

            public Builder clearOneof(com.google.protobuf.Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }

            public Builder setRepeatedField(com.google.protobuf.Descriptors.FieldDescriptor field,
                int index, Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }

            public Builder addRepeatedField(com.google.protobuf.Descriptors.FieldDescriptor field,
                Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }

            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof com.iGap.proto.ProtoError.ErrorResponse) {
                    return mergeFrom((com.iGap.proto.ProtoError.ErrorResponse) other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(com.iGap.proto.ProtoError.ErrorResponse other) {
                if (other == com.iGap.proto.ProtoError.ErrorResponse.getDefaultInstance()) {
                    return this;
                }
                if (other.hasResponse()) {
                    mergeResponse(other.getResponse());
                }
                if (other.getMajorCode() != 0) {
                    setMajorCode(other.getMajorCode());
                }
                if (other.getMinorCode() != 0) {
                    setMinorCode(other.getMinorCode());
                }
                if (other.getWait() != 0) {
                    setWait(other.getWait());
                }
                onChanged();
                return this;
            }

            public final boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
                com.iGap.proto.ProtoError.ErrorResponse parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage =
                        (com.iGap.proto.ProtoError.ErrorResponse) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            /**
             * <code>optional .proto.Response response = 1;</code>
             */
            public boolean hasResponse() {
                return responseBuilder_ != null || response_ != null;
            }

            /**
             * <code>optional .proto.Response response = 1;</code>
             */
            public com.iGap.proto.ProtoResponse.Response getResponse() {
                if (responseBuilder_ == null) {
                    return response_ == null
                        ? com.iGap.proto.ProtoResponse.Response.getDefaultInstance() : response_;
                } else {
                    return responseBuilder_.getMessage();
                }
            }

            /**
             * <code>optional .proto.Response response = 1;</code>
             */
            public Builder setResponse(
                com.iGap.proto.ProtoResponse.Response.Builder builderForValue) {
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
            public Builder setResponse(com.iGap.proto.ProtoResponse.Response value) {
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
            public Builder mergeResponse(com.iGap.proto.ProtoResponse.Response value) {
                if (responseBuilder_ == null) {
                    if (response_ != null) {
                        response_ = com.iGap.proto.ProtoResponse.Response.newBuilder(response_)
                            .mergeFrom(value)
                            .buildPartial();
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
            public com.iGap.proto.ProtoResponse.Response.Builder getResponseBuilder() {

                onChanged();
                return getResponseFieldBuilder().getBuilder();
            }

            /**
             * <code>optional .proto.Response response = 1;</code>
             */
            public com.iGap.proto.ProtoResponse.ResponseOrBuilder getResponseOrBuilder() {
                if (responseBuilder_ != null) {
                    return responseBuilder_.getMessageOrBuilder();
                } else {
                    return response_ == null
                        ? com.iGap.proto.ProtoResponse.Response.getDefaultInstance() : response_;
                }
            }

            /**
             * <code>optional .proto.Response response = 1;</code>
             */
            private com.google.protobuf.SingleFieldBuilderV3<com.iGap.proto.ProtoResponse.Response, com.iGap.proto.ProtoResponse.Response.Builder, com.iGap.proto.ProtoResponse.ResponseOrBuilder> getResponseFieldBuilder() {
                if (responseBuilder_ == null) {
                    responseBuilder_ =
                        new com.google.protobuf.SingleFieldBuilderV3<com.iGap.proto.ProtoResponse.Response, com.iGap.proto.ProtoResponse.Response.Builder, com.iGap.proto.ProtoResponse.ResponseOrBuilder>(
                            getResponse(), getParentForChildren(), isClean());
                    response_ = null;
                }
                return responseBuilder_;
            }

            /**
             * <code>optional uint32 major_code = 2;</code>
             */
            public int getMajorCode() {
                return majorCode_;
            }

            /**
             * <code>optional uint32 major_code = 2;</code>
             */
            public Builder setMajorCode(int value) {

                majorCode_ = value;
                onChanged();
                return this;
            }

            /**
             * <code>optional uint32 major_code = 2;</code>
             */
            public Builder clearMajorCode() {

                majorCode_ = 0;
                onChanged();
                return this;
            }

            /**
             * <code>optional uint32 minor_code = 3;</code>
             */
            public int getMinorCode() {
                return minorCode_;
            }

            /**
             * <code>optional uint32 minor_code = 3;</code>
             */
            public Builder setMinorCode(int value) {

                minorCode_ = value;
                onChanged();
                return this;
            }

            /**
             * <code>optional uint32 minor_code = 3;</code>
             */
            public Builder clearMinorCode() {

                minorCode_ = 0;
                onChanged();
                return this;
            }

            /**
             * <code>optional uint32 wait = 4;</code>
             */
            public int getWait() {
                return wait_;
            }

            /**
             * <code>optional uint32 wait = 4;</code>
             */
            public Builder setWait(int value) {

                wait_ = value;
                onChanged();
                return this;
            }

            /**
             * <code>optional uint32 wait = 4;</code>
             */
            public Builder clearWait() {

                wait_ = 0;
                onChanged();
                return this;
            }

            public final Builder setUnknownFields(
                final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }

            public final Builder mergeUnknownFields(
                final com.google.protobuf.UnknownFieldSet unknownFields) {
                return this;
            }

            // @@protoc_insertion_point(builder_scope:proto.ErrorResponse)
        }
    }

    // @@protoc_insertion_point(outer_class_scope)
}
