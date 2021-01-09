graph [
  node [
    id 0
    label "0"
    type 2
    prc 3
  ]
  node [
    id 1
    label "1"
    type 2
    prc 4
  ]
  node [
    id 2
    label "2"
    type 1
    prc 1
    ant 10
    prb 3
    x 87
    y 116
  ]
  node [
    id 3
    label "3"
    type 1
    prc 1
    ant 10
    prb 3
    x 31
    y 97
  ]
  node [
    id 4
    label "4"
    type 1
    prc 4
    ant 4
    prb 2
    x 117
    y 37
  ]
  edge [
    source 0
    target 4
    bandwith 430
    delay 135
  ]
  edge [
    source 1
    target 3
    bandwith 422
    delay 499
  ]
  edge [
    source 1
    target 2
    bandwith 465
    delay 160
  ]
]
