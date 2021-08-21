package com.paascloud.provider.oss.controller.rpc;

import com.paascloud.core.web.api.R;
import com.paascloud.provider.api.oss.model.ao.AttributeGreenRpcAO;
import com.paascloud.provider.api.oss.model.dto.AttributeGreenResultDTO;
import com.paascloud.provider.api.oss.service.GreenRpcService;
import com.paascloud.provider.api.oss.service.OssRpcService;
import com.paascloud.provider.oss.service.GreenService;
import com.paascloud.provider.oss.service.OssService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rpc/oss")
public class OssRpcController implements OssRpcService, GreenRpcService {

    private final OssService ossService;
    private final GreenService greenService;

    public OssRpcController(OssService ossService,
                            GreenService greenService){
        this.ossService = ossService;
        this.greenService = greenService;
    }

    @GetMapping("/getOjectBrowseUrl")
    @Override
    public R<String> getOjectBrowseUrl(@RequestParam String objectKey){
        return R.ok(ossService.getOjectBrowseUrl(objectKey));
    }

    @GetMapping("/listOjectBrowseUrl")
    @Override
    public R<Map<String, String>> listOjectBrowseUrl(@RequestParam List<String> objectKeys) {
        return R.ok(ossService.listOjectBrowseUrl(objectKeys));
    }

    @PostMapping("/greenAttributeAsync")
    @Override
    public R<Void> greenAttributeAsync(@RequestBody AttributeGreenRpcAO greenData) {
        greenService.greenAttributeAsync(greenData);
        return R.ok();
    }

    @PostMapping("/greenAttributeSync")
    @Override
    public R<AttributeGreenResultDTO> greenAttributeSync(@RequestBody AttributeGreenRpcAO attribute) {
        return R.ok(greenService.greenAttributeSync(attribute));
    }
}
