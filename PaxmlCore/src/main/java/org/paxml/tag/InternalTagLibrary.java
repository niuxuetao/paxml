/**
 * This file is part of PaxmlCore.
 *
 * PaxmlCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PaxmlCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with PaxmlCore.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.paxml.tag;

import org.paxml.assertion.AssertEqualTag;
import org.paxml.assertion.AssertFalseTag;
import org.paxml.assertion.AssertNotEqualTag;
import org.paxml.assertion.AssertNotNullTag;
import org.paxml.assertion.AssertNotTag;
import org.paxml.assertion.AssertNullTag;
import org.paxml.assertion.AssertPatternTag;
import org.paxml.assertion.AssertTag;
import org.paxml.assertion.AssertTrueTag;
import org.paxml.assertion.FailTag;
import org.paxml.bean.AntTag;
import org.paxml.bean.AppendTag;
import org.paxml.bean.BeanCreationTag;
import org.paxml.bean.BundleTag;
import org.paxml.bean.CallTag;
import org.paxml.bean.ConcatTag;
import org.paxml.bean.ConfirmTag;
import org.paxml.bean.CountTag;
import org.paxml.bean.DateTag;
import org.paxml.bean.DeleteSecretTag;
import org.paxml.bean.EmailTag;
import org.paxml.bean.FindConstTag;
import org.paxml.bean.FlattenTag;
import org.paxml.bean.FromJsonTag;
import org.paxml.bean.FromXmlTag;
import org.paxml.bean.GroovyTag;
import org.paxml.bean.HttpTag;
import org.paxml.bean.JoinTag;
import org.paxml.bean.ListTag;
import org.paxml.bean.LogTag;
import org.paxml.bean.PauseTag;
import org.paxml.bean.PrintTag;
import org.paxml.bean.PropertiesTag;
import org.paxml.bean.RandomTag;
import org.paxml.bean.ReadFileTag;
import org.paxml.bean.RestTag;
import org.paxml.bean.ResultTag;
import org.paxml.bean.ReturnTag;
import org.paxml.bean.RunTag;
import org.paxml.bean.SecretTag;
import org.paxml.bean.SetLocaleTag;
import org.paxml.bean.SetTag;
import org.paxml.bean.SftpTag;
import org.paxml.bean.SoapTag;
import org.paxml.bean.SplitTag;
import org.paxml.bean.SpringApplicationContextTag;
import org.paxml.bean.ToJsonTag;
import org.paxml.bean.ToXmlTag;
import org.paxml.bean.TrimTag;
import org.paxml.bean.UuidTag;
import org.paxml.bean.WriteFileTag;
import org.paxml.bean.XpathSelectTag;
import org.paxml.control.ElseTag;
import org.paxml.control.ExitTag;
import org.paxml.control.IfTag;
import org.paxml.control.IterateTag;
import org.paxml.control.MutexTag;
import org.paxml.el.CollectionUtilsFactory;
import org.paxml.el.DateUtilsFactory;
import org.paxml.el.StringUtilsFactory;
import org.paxml.el.UtilFunctions;
import org.paxml.tag.invoker.ExpressionTag;
import org.paxml.tag.sql.DdlTag;
import org.paxml.tag.sql.SqlDataSourceTag;
import org.paxml.tag.sql.SqlInsertTag;
import org.paxml.tag.sql.SqlQueryTag;
import org.paxml.tag.sql.SqlTag;

/**
 * Internal tag library.
 * 
 * @author Xuetao Niu
 * 
 */
public final class InternalTagLibrary extends DefaultTagLibrary {
    /**
     * The single instance.
     */
    public static final InternalTagLibrary INSTANCE = new InternalTagLibrary();

    private InternalTagLibrary() {
        super();

        // tags
        registerTag(GroovyTag.class);
        registerTag(FindConstTag.class);
        registerTag(RunTag.class);
        registerTag(CallTag.class);
        registerTag(IterateTag.class);
        registerTag(IfTag.class);
        registerTag(ElseTag.class);
        registerTag(ResultTag.class);
        registerTag(ReturnTag.class);
        registerTag(ConstTag.class);
        registerTag(DefaultConstTag.class);
        registerTag(ListTag.class);
        registerTag(SetTag.class);
        registerTag(AppendTag.class);
        registerTag(ConfirmTag.class);
        
        registerTag(MutexTag.class);
        registerTag(ReadFileTag.class);
        registerTag(WriteFileTag.class);

        registerTag(XpathSelectTag.class);
        registerTag(FlattenTag.class);
        registerTag(ConcatTag.class);
        registerTag(TrimTag.class);
        registerTag(SplitTag.class);
        registerTag(JoinTag.class);

        registerTag(DateTag.class);
        registerTag(PropertiesTag.class);
        registerTag(BundleTag.class);
        registerTag(SetLocaleTag.class);

        registerTag(PrintTag.class);
        registerTag(LogTag.class);
        registerTag(ExitTag.class);
        registerTag(PauseTag.class);
        registerTag(ExpressionTag.class);

        registerTag(AssertTag.class);
        registerTag(AssertNotTag.class);
        registerTag(AssertEqualTag.class);
        registerTag(AssertNotEqualTag.class);
        registerTag(FailTag.class);
        registerTag(AssertNullTag.class);
        registerTag(AssertNotNullTag.class);
        registerTag(AssertTrueTag.class);
        registerTag(AssertFalseTag.class);
        registerTag(AssertPatternTag.class);

        registerTag(RandomTag.class);
        registerTag(UuidTag.class);
        registerTag(SoapTag.class);
        registerTag(BeanCreationTag.class);
        registerTag(SpringApplicationContextTag.class);

        registerTag(SqlDataSourceTag.class);
        registerTag(SqlTag.class);
        registerTag(SqlInsertTag.class);
        registerTag(SqlQueryTag.class);
        registerTag(DdlTag.class);
        
        registerTag(CountTag.class);
        registerTag(LiteralTag.class);
        registerTag(HttpTag.class);
        registerTag(RestTag.class);
        registerTag(AntTag.class);
        registerTag(SftpTag.class);
        
        registerTag(FromXmlTag.class);
        registerTag(FromJsonTag.class);
        registerTag(ToXmlTag.class);
        registerTag(ToJsonTag.class);
        
        registerTag(SecretTag.class);
        registerTag(DeleteSecretTag.class);
        
        registerTag(EmailTag.class);
        
        // util functions
        registerUtil(UtilFunctions.class);
        registerUtil(CollectionUtilsFactory.class);
        registerUtil(StringUtilsFactory.class);
        registerUtil(DateUtilsFactory.class);
    }

}
