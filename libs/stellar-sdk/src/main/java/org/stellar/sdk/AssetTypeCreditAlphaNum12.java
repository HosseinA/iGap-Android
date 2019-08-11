package org.stellar.sdk;

import org.stellar.sdk.xdr.*;

/**
 * Represents all assets with codes 5-12 characters long.
 * @see <a href="https://www.stellar.org/developers/learn/concepts/assets.html" target="_blank">Assets</a>
 */
public final class AssetTypeCreditAlphaNum12 extends AssetTypeCreditAlphaNum {

  /**
   * Class constructor
   * @param code Asset code
   * @param issuer Asset issuer
   */
  public AssetTypeCreditAlphaNum12(String code, String issuer) {
    super(code, issuer);
    if (code.length() < 5 || code.length() > 12) {
      throw new AssetCodeLengthInvalidException();
    }
  }

  @Override
  public String getType() {
    return "credit_alphanum12";
  }

  @Override
  public org.stellar.sdk.xdr.Asset toXdr() {
    org.stellar.sdk.xdr.Asset xdr = new org.stellar.sdk.xdr.Asset();
    xdr.setDiscriminant(AssetType.ASSET_TYPE_CREDIT_ALPHANUM12);
    org.stellar.sdk.xdr.Asset.AssetAlphaNum12 credit = new org.stellar.sdk.xdr.Asset.AssetAlphaNum12();
    AssetCode12 assetCode12 = new AssetCode12();
    assetCode12.setAssetCode12(Util.paddedByteArray(mCode, 12));
    credit.setAssetCode(assetCode12);
    credit.setIssuer(StrKey.encodeToXDRAccountId(mIssuer));
    xdr.setAlphaNum12(credit);
    return xdr;
  }
}
