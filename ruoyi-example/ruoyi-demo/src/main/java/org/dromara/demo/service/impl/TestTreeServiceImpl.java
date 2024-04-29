package org.dromara.demo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.mybatisflex.annotation.UseDataSource;
import com.mybatisflex.core.query.QueryWrapper;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.demo.domain.TestTree;
import org.dromara.demo.domain.bo.TestTreeBo;
import org.dromara.demo.domain.vo.TestTreeVo;
import org.dromara.demo.mapper.TestTreeMapper;
import org.dromara.demo.service.ITestTreeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.dromara.demo.domain.table.TestTreeTableDef.TEST_TREE;

/**
 * 测试树表Service业务层处理
 *
 * @author Lion Li
 * @date 2021-07-26
 */
@UseDataSource("slave") // 切换从库查询
@RequiredArgsConstructor
@Service
public class TestTreeServiceImpl implements ITestTreeService {

    private final TestTreeMapper baseMapper;

    @Override
    public TestTreeVo queryById(Long id) {
        return baseMapper.selectOneWithRelationsByIdAs(id, TestTreeVo.class);
    }

    // @DS("slave") // 切换从库查询
    @Override
    public List<TestTreeVo> queryList(TestTreeBo bo) {
        QueryWrapper lqw = buildQueryWrapper(bo);
        return baseMapper.selectListByQueryAs(lqw, TestTreeVo.class);
    }

    private QueryWrapper buildQueryWrapper(TestTreeBo bo) {
        Map<String, Object> params = bo.getParams();
        return QueryWrapper.create()
            .from(TEST_TREE)
            .where(TEST_TREE.TREE_NAME.like(bo.getTreeName()))
            .and(TEST_TREE.CREATE_TIME.between(params.get("beginCreateTime"), params.get("endCreateTime"), params.get("beginCreateTime") != null && params.get("endCreateTime") != null))
            .orderBy(TEST_TREE.ID, true);
    }

    @Override
    public Boolean insertByBo(TestTreeBo bo) {
        TestTree add = BeanUtil.toBean(bo, TestTree.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    @Override
    public Boolean updateByBo(TestTreeBo bo) {
        TestTree update = BeanUtil.toBean(bo, TestTree.class);
        validEntityBeforeSave(update);
        return baseMapper.update(update) > 0;
    }

    /**
     * 保存前的数据校验
     *
     * @param entity 实体类数据
     */
    private void validEntityBeforeSave(TestTree entity) {
        // TODO 做一些数据校验,如唯一约束
    }

    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            // TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchByIds(ids) > 0;
    }
}
