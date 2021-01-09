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
    prc 5
  ]
  node [
    id 2
    label "2"
    type 1
    prc 4
    ant 8
    prb 3
    x 32
    y 29
  ]
  node [
    id 3
    label "3"
    type 1
    prc 5
    ant 7
    prb 3
    x 37
    y 61
  ]
  node [
    id 4
    label "4"
    type 1
    prc 3
    ant 5
    prb 5
    x 21
    y 44
  ]
  edge [
    source 0
    target 4
    bandwith 298
    delay 323
  ]
  edge [
    source 1
    target 2
    bandwith 663
    delay 121
  ]
  edge [
    source 1
    target 3
    bandwith 419
    delay 435
  ]
]
